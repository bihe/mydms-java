package net.binggl.mydms.test.gdrive;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;

import com.google.api.client.auth.oauth2.Credential;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import net.binggl.mydms.MydmsApplication;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.gdrive.client.GDriveClient;

public class GDriveClientTest {

	private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("testing.yml");
	
	@ClassRule
    public static final DropwizardAppRule<MydmsConfiguration> RULE = new DropwizardAppRule<MydmsConfiguration>(
            MydmsApplication.class, CONFIG_PATH);
	
//	@Test
//    public void callGDriveClient() throws IOException {
//		GDriveClient client = new GDriveClient(RULE.getConfiguration());
//		String redirectUrl = client.getRedirectUrl();
//		assertNotNull(redirectUrl);
//		
//		Credential credential = client.getCredentials("code");
//		assertNotNull(credential);
//	}
}
