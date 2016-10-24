package net.binggl.mydms.tags;

import com.google.inject.Inject;

import net.binggl.mydms.hibernate.TransactionProvider;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

public class TagHealthCheck extends NamedHealthCheck {

	private TagStore store;
	private TransactionProvider txProvider;
	private static final String HealthCheckName = "any_tags";

	@Inject
	public TagHealthCheck(TagStore store, TransactionProvider txProvider) {
		this.store = store;
		this.txProvider = txProvider;
	}

	@Override
	protected Result check() throws Exception {
		
		boolean any = txProvider.transactional(session -> {
			return store.any();
		});
		
		if(!any) {
			return Result.unhealthy("No tags available!");
		}
		return Result.healthy();
	}

	@Override
	public String getName() {
		return HealthCheckName;
	}
}