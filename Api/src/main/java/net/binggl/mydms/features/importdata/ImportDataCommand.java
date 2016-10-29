package net.binggl.mydms.features.importdata;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import liquibase.util.file.FilenameUtils;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.documents.Document;
import net.binggl.mydms.features.documents.DocumentStore;
import net.binggl.mydms.features.importdata.models.ImportDocument;
import net.binggl.mydms.features.importdata.models.ImportTagSender;
import net.binggl.mydms.features.importdata.models.PathResult;
import net.binggl.mydms.features.initialdata.InitialDataCommand;
import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.senders.SenderStore;
import net.binggl.mydms.features.tags.Tag;
import net.binggl.mydms.features.tags.TagStore;
import net.binggl.mydms.hibernate.TransactionProvider;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class ImportDataCommand extends EnvironmentCommand<MydmsConfiguration> {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitialDataCommand.class);
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	private TagStore tagStore;
	private SenderStore senderStore;
	private DocumentStore documentStore;
	private TransactionProvider txProvider;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ImportDataCommand(Application application) {
		super(application, "importData", "Import the given file of the given type into the database!");
	}

	@Override
	public void configure(Subparser subparser) {
		addFileArgument(subparser);

		// Add a command line option
		subparser.addArgument("--delete-database-contents").dest("forceDelete").type(Boolean.class).required(true)
			.help("Delete the given database contents before the import.").required(true);
		
		subparser.addArgument("-t", "--tags").dest("tags").type(String.class).required(true)
				.help("The path to the tags JSON file").required(false);

		subparser.addArgument("-s", "--senders").dest("senders").type(String.class).required(true)
				.help("The path to the senders JSON file").required(false);

		subparser.addArgument("-d", "--documents").dest("documents").type(String.class).required(true)
				.help("The path to the documents JSON file").required(false);
	}

	@Override
	protected void run(Environment environment, Namespace namespace, MydmsConfiguration configuration)
			throws Exception {
		LOGGER.info("Will run command: importData!");

		txProvider.transactional(session -> {
			
			Boolean forceDelete = namespace.getBoolean("forceDelete");
			if(forceDelete) {
				LOGGER.warn("Import will clear database!");
				
				session.createSQLQuery("DELETE FROM DOCUMENTS_TO_TAGS").executeUpdate();
				session.createSQLQuery("DELETE FROM DOCUMENTS_TO_SENDERS").executeUpdate();
				session.createSQLQuery("DELETE FROM TAGS").executeUpdate();
				session.createSQLQuery("DELETE FROM SENDERS").executeUpdate();
				session.createSQLQuery("DELETE FROM DOCUMENTS").executeUpdate();
			}
			
		
			String tags = namespace.getString("tags");
			if(StringUtils.isNotEmpty(tags)) {
				PathResult result = isAcessablePath(tags);
				if(result.validPath) {
					this.importSimple(result.canonicalPath, name -> {
						Tag t = tagStore.save(new Tag(name));
						return (t != null);
					});
				}
			}
			
			String senders = namespace.getString("senders");
			if(StringUtils.isNotEmpty(senders)) {
				PathResult result = isAcessablePath(senders);
				if(result.validPath) {
					this.importSimple(result.canonicalPath, name -> {
						Sender s = senderStore.save(new Sender(name));
						return (s != null);
					});
				}
			}
	
			String documents = namespace.getString("documents");
			if(StringUtils.isNotEmpty(documents)) {
				PathResult result = isAcessablePath(documents);
				if(result.validPath) {
					this.importDocuments(result.canonicalPath);
				}
			}
			
			return null;
		});
	}
	
	private Boolean importDocuments(String path) {
		boolean result = false;
		try {
			LOGGER.info("Will import documents into database!");
			
			Path p = FileSystems.getDefault().getPath(path);
			String contents = new String (Files.readAllBytes(p),Charset.forName("UTF-8")); 
			if(StringUtils.isEmpty(contents)) {
				LOGGER.warn("Empty import file!");
				return false;
			}
			
			List<ImportDocument> items = MAPPER.readValue(contents, new TypeReference<List<ImportDocument>>() {});
			if(items != null) {
				LOGGER.info("Got {} items", items.size());
				
				Document document = null;
				Optional<Tag> t;
				Optional<Sender> s;
				for(ImportDocument item : items) {
					document = new Document();
					document.setId(UUID.randomUUID());
					document.setAlternativeId(item.getAlternativeId());
					document.setAmount(item.getAmount());
					document.setCreated(item.getCreated().toDate());
					document.setFileName(item.getFileName());
					document.setModified(item.getModified().toDate());
					document.setPreviewLink(item.getPreviewLink());
					document.setTitle(item.getTitle());
					
					for(ImportTagSender tag : item.getTags()) {
						t = tagStore.tagByName(tag.getName());
						if(t.isPresent()) {
							document.getTags().add(t.get());
						}
					}
					
					for(ImportTagSender sender : item.getSenders()) {
						s = senderStore.senderByName(sender.getName());
						if(s.isPresent()) {
							document.getSenders().add(s.get());
						}
					}
					
					Document d = documentStore.save(document);
					if(d == null)
						return false;
				}
				
				LOGGER.info("Imported {} documents", items.size());
				
			}
			
		} catch(Exception EX) {
			LOGGER.error("Could not import documents {}", EX.getMessage(), EX);
		}
		return result;
	}
	
	private Boolean importSimple(String path, Function<String, Boolean> callback) {
		
		try {
			LOGGER.info("Will import items into database!");
			
			Path p = FileSystems.getDefault().getPath(path);
			String contents = new String (Files.readAllBytes(p),Charset.forName("UTF-8")); 
			if(StringUtils.isEmpty(contents)) {
				LOGGER.warn("Empty import file!");
				return false;
			}
			
			List<ImportTagSender> items = MAPPER.readValue(contents, new TypeReference<List<ImportTagSender>>() {});
			if(items != null) {
				LOGGER.info("Got {} items", items.size());
				
				for(ImportTagSender item : items) {
					if(callback.apply(item.getName()) == false)
						return false;
				}
				
				LOGGER.info("Imported {} items", items.size());
			}
		} catch(Exception EX) {
			LOGGER.error("Could not import items {}", EX.getMessage(), EX);
		}
		return true;
	}
	
	private PathResult isAcessablePath(String path) {
		try {
			String normalized = FilenameUtils.normalize(path);
			File file = new File(normalized);
			return new PathResult(file.exists(), file.getCanonicalPath());
		} catch(Exception EX) {
			LOGGER.error("Could not check file path {}", EX.getMessage(), EX);
		}
		return new PathResult(false, null);
	}

	public TagStore getTagStore() {
		return tagStore;
	}
	@Inject
	public void setTagStore(TagStore tagStore) {
		this.tagStore = tagStore;
	}

	public SenderStore getSenderStore() {
		return senderStore;
	}
	@Inject
	public void setSenderStore(SenderStore senderStore) {
		this.senderStore = senderStore;
	}

	public DocumentStore getDocumentStore() {
		return documentStore;
	}
	@Inject
	public void setDocumentStore(DocumentStore documentStore) {
		this.documentStore = documentStore;
	}

	public TransactionProvider getTxProvider() {
		return txProvider;
	}
	@Inject
	public void setTxProvider(TransactionProvider txProvider) {
		this.txProvider = txProvider;
	}
}
