package net.binggl.mydms.application;

import org.joda.time.LocalDate;

import io.dropwizard.views.View;

public class Mydms403View extends View implements Globals {

	public Mydms403View() {
		super("403.ftl");
	}

	public String getAppName() {
		return APPLICATION_NAME;
	}
	
	public String getYear() {
		int year = new LocalDate().getYear();
		return String.format("%s", year);
	}
}
