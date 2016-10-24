package net.binggl.mydms.cmd;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import net.binggl.mydms.MydmsConfiguration;
import net.binggl.mydms.hibernate.TransactionProvider;
import net.binggl.mydms.senders.Sender;
import net.binggl.mydms.senders.SenderStore;
import net.binggl.mydms.tags.Tag;
import net.binggl.mydms.tags.TagStore;
import net.sourceforge.argparse4j.inf.Namespace;

public class InitialDataCommand extends EnvironmentCommand<MydmsConfiguration> {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitialDataCommand.class);

	private TagStore tagStore;
	private SenderStore senderStore;
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
				
				session.createSQLQuery("DELETE FROM TAG").executeUpdate();
				session.createSQLQuery("DELETE FROM SENDER").executeUpdate();

				for (int i = 1; i < 11; i++) {
					tagStore.save(new Tag(String.format("tag%d", i)));
				}

				for (int i = 1; i < 11; i++) {
					senderStore.save(new Sender(String.format("sender%d", i)));
				}
				
				List<Tag> tags = tagStore.findAll();
				List<Sender> senders = senderStore.findAll();

				if (tags != null) {
					LOGGER.info(String.format("Created %d tags.", tags.size()));
				}
				if (senders != null) {
					LOGGER.info(String.format("Created %d senders.", senders.size()));
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
}