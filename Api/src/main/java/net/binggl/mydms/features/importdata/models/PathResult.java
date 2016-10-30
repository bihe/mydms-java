package net.binggl.mydms.features.importdata.models;

public class PathResult {
	public boolean validPath = false;
	public String canonicalPath = null;

	public PathResult(boolean validPath, String canonicalPath) {
		super();
		this.validPath = validPath;
		this.canonicalPath = canonicalPath;
	}
}