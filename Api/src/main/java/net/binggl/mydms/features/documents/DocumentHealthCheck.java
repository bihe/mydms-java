package net.binggl.mydms.features.documents;

import com.google.inject.Inject;

import net.binggl.mydms.hibernate.TransactionProvider;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

public class DocumentHealthCheck extends NamedHealthCheck {

	private DocumentStore store;
	private TransactionProvider txProvider;
	private static final String HealthCheckName = "any_documents";

	@Inject
	public DocumentHealthCheck(DocumentStore store, TransactionProvider txProvider) {
		this.store = store;
		this.txProvider = txProvider;
	}

	@Override
	protected Result check() throws Exception {
		
		boolean any = txProvider.transactional(session -> {
			return store.any();
		});
		
		if(!any) {
			return Result.unhealthy("No documents available!");
		}
		return Result.healthy();
	}

	@Override
	public String getName() {
		return HealthCheckName;
	}
}