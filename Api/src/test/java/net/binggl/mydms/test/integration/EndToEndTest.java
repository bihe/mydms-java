package net.binggl.mydms.test.integration;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import net.binggl.mydms.MydmsApplication;
import net.binggl.mydms.config.MydmsConfiguration;
import net.binggl.mydms.features.tags.Tag;

public class EndToEndTest {
	
	
	private static final String TMP_FILE = createTempFile();
    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("testing.yml");

    @ClassRule
    public static final DropwizardAppRule<MydmsConfiguration> ApplicationRule = new DropwizardAppRule<MydmsConfiguration>(
            MydmsApplication.class, CONFIG_PATH,
            ConfigOverride.config("database.url", "jdbc:h2:" + TMP_FILE));
    
    private Client client;

    @BeforeClass
    public static void migrateDb() throws Exception {
        ApplicationRule.getApplication().run("db", "migrate", CONFIG_PATH);
    }

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    private static String createTempFile() {
        try {
            return File.createTempFile("test-example", null).getAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
	
    @Test
    @Ignore
    public void testSearchTags() throws Exception {
        final Optional<String> name = Optional.of("tag");
        
        @SuppressWarnings("unchecked")
		final List<Tag> tags = client.target("http://localhost:" + ApplicationRule.getLocalPort() + "/api/tags/search")
                .queryParam("name", name.get())
                .request()
                .get(List.class);
        
        assertNotNull(tags);
    }
}
