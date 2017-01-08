package net.binggl.mydms.features.initialdata;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.documents.DocumentStore;
import net.binggl.mydms.features.documents.models.Document;
import net.binggl.mydms.features.documents.models.DocumentsSenders;
import net.binggl.mydms.features.documents.models.DocumentsTags;
import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.senders.SenderStore;
import net.binggl.mydms.features.tags.Tag;
import net.binggl.mydms.features.tags.TagStore;
import net.binggl.mydms.hibernate.TransactionProvider;
import net.sourceforge.argparse4j.inf.Namespace;

public class InitialDataCommand extends EnvironmentCommand<MydmsConfiguration> {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitialDataCommand.class);

	private TagStore tagStore;
	private SenderStore senderStore;
	private DocumentStore documentStore;
	private TransactionProvider txProvider;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public InitialDataCommand(Application application) {
		super(application, "initialData", "Create initial entries in the given database!");
	}

	@Override
	protected void run(Environment environment, Namespace namespace, MydmsConfiguration configuration)
			throws Exception {
		LOGGER.info("Will run command: initialData!");

		if (configuration.getApplication().isInitialData()) {
			LOGGER.info("Will create initial data to play with.");

			txProvider.transactional(session -> {

				LOGGER.info("Will delete database entries.");

				session.createSQLQuery("DELETE FROM DOCUMENTS_TO_TAGS").executeUpdate();
				session.createSQLQuery("DELETE FROM DOCUMENTS_TO_SENDERS").executeUpdate();
				session.createSQLQuery("DELETE FROM TAGS").executeUpdate();
				session.createSQLQuery("DELETE FROM SENDERS").executeUpdate();
				session.createSQLQuery("DELETE FROM DOCUMENTS").executeUpdate();

				for (int i = 1; i < 11; i++) {
					tagStore.save(new Tag(String.format("tag%d", i)));
				}

				for (int i = 1; i < 11; i++) {
					senderStore.save(new Sender(String.format("sender%d", i)));
				}

				List<Tag> tag1 = tagStore.searchByName("tag1");
				List<Sender> sender2 = senderStore.searchByName("sender2");

				for (int i = 1; i < 11; i++) {
					Document document = new Document(String.format("document%d", i), "filename", String.format("alternativeId%d", i),
							"previewLink", 1.0);
					
					for(Tag tag : tag1) {
						DocumentsTags tagRef = new DocumentsTags(document, tag);
						document.getTags().add(tagRef);
					}
					
					for(Sender sender : sender2) {
						DocumentsSenders senderRef = new DocumentsSenders(document, sender);
						document.getSenders().add(senderRef);
					}
					
					documentStore.save(document);
				}

				List<Tag> tags = tagStore.findAll();
				List<Sender> senders = senderStore.findAll();
				List<Document> documents = documentStore.findAll();

				if (tags != null) {
					LOGGER.info(String.format("Created %d tags.", tags.size()));
				}
				if (senders != null) {
					LOGGER.info(String.format("Created %d senders.", senders.size()));
				}
				if (documents != null) {
					LOGGER.info(String.format("Created %d documents.", documents.size()));
				}

				return null;
			});

		} else {
			LOGGER.warn("Won't create data, configuration setting 'application.initialData' not set!");
		}
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

	public TransactionProvider getTxProvider() {
		return txProvider;
	}

	@Inject
	public void setTxProvider(TransactionProvider txProvider) {
		this.txProvider = txProvider;
	}

	public DocumentStore getDocumentStore() {
		return documentStore;
	}

	@Inject
	public void setDocumentStore(DocumentStore documentStore) {
		this.documentStore = documentStore;
	}
}
