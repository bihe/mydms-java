package net.binggl.mydms.features.gdrive.store;

import net.binggl.mydms.features.gdrive.models.GDriveCredential;

/**
 * definition of a store to persist the google drive
 * credentials 
 * @author henrik
 */
public interface GDriveCredentialStore {

	/**
	 * persist the credentials
	 * @param userToken - the credentials are associated to a given userId
	 * @param credential - the credentials to save 
	 */
	void save(String userToken, GDriveCredential credential);
	
	/**
	 * load the credentials fo the given userToken
	 * @param userToken - the credentials are associated to a given userId
	 * @return
	 */
	GDriveCredential load(String userToken);
	
	/**
	 * delete the saved credentials
	 * @param userToken
	 */
	void clearCredentials(String userToken);
	
	/**
	 * check if credentials are available
	 * @param userToken
	 * @return
	 */
	boolean isCredentialAvailable(String userToken);
}
