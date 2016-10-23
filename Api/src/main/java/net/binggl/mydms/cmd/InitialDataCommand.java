package net.binggl.mydms.cmd;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import net.binggl.mydms.MydmsConfiguration;
import net.binggl.mydms.bootstrap.ManagedSessionTransactionProvider;
import net.binggl.mydms.bootstrap.TransactionProvider;
import net.binggl.mydms.senders.Sender;
import net.binggl.mydms.senders.SenderStore;
import net.binggl.mydms.tags.Tag;
import net.binggl.mydms.tags.TagStore;
import net.sourceforge.argparse4j.inf.Namespace;

public class InitialDataCommand extends EnvironmentCommand<MydmsConfiguration> {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(InitialDataCommand.class);
	
	@Inject
	private SessionFactory sessionFactory;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public InitialDataCommand(Application application) {
		super(application, "initialData", "Create initial entries in the given database!");
	}

	@Override
	protected void run(Environment environment, Namespace namespace, MydmsConfiguration configuration)
			throws Exception {
		LOGGER.info("Will run command: initialData!");

		if(configuration.getApplication().isInitialData()) {
			LOGGER.info("Will create initial data to play with.");
			
			
			
			try (Session session = sessionFactory.openSession()) {
				final Transaction txn = session.beginTransaction();
				try {
					
					LOGGER.info("Will delete database entries.");
					
					session.createSQLQuery("DELETE FROM TAG").executeUpdate();
					session.createSQLQuery("DELETE FROM SENDER").executeUpdate();
					
					for(int i=1;i<11;i++) {
						Tag tag = new Tag(String.format("tag%d", i));
						session.save(tag);
					}
					
					for(int i=1;i<11;i++) {
						Sender sender = new Sender(String.format("sender%d", i));
						session.save(sender);
					}
					
					txn.commit();
					
					LOGGER.info("Created new database entries!");
					
				} catch (Exception e) {
					if (txn.getStatus().canRollback()) {
						txn.rollback();
					}
					throw e;
				}
			}
			
			TransactionProvider transactionProvider = new ManagedSessionTransactionProvider(sessionFactory);
			TagStore tagStore = new TagStore(sessionFactory);
			SenderStore senderStore = new SenderStore(sessionFactory);
			
			transactionProvider.transactional(session -> {
				List<Tag> tags = tagStore.findAll();
				List<Sender> senders = senderStore.findAll();
				
				if(tags != null) {
					LOGGER.info(String.format("Created %d tags.", tags.size()));
				}
				if(senders != null) {
					LOGGER.info(String.format("Created %d senders.", senders.size()));
				}
				
				return null;
			});
			
		} else {
			LOGGER.warn("Won't create data, configuration setting 'application.initialData' not set!");
		}
		
	}
}
