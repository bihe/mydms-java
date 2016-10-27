package net.binggl.mydms.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class MydmsConfiguration extends Configuration {
    
	@Valid
	@NotNull
	private ApplicationConfiguration application = new ApplicationConfiguration();
	
	@JsonProperty("application")
	public ApplicationConfiguration getApplication() {
		return application;
	}

	@Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}
