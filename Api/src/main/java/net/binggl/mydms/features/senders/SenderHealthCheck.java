package net.binggl.mydms.features.senders;

import com.google.inject.Inject;

import net.binggl.mydms.hibernate.TransactionProvider;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

public class SenderHealthCheck extends NamedHealthCheck {

	private static final String HealthCheckName = "any_senders";
	private SenderStore store;
	private TransactionProvider txProvider;
	
	@Inject
	public SenderHealthCheck(SenderStore store, TransactionProvider txProvider) {
		this.store = store;
		this.txProvider = txProvider;
	}

	@Override
	protected Result check() throws Exception {
		
		boolean any = txProvider.transactional(session -> {
			return store.any();
		});
		
		if(!any) {
			return Result.unhealthy("No senders available!");
		}
		return Result.healthy();
	}


	@Override
	public String getName() {
		return HealthCheckName;
	}
}