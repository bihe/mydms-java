package net.binggl.mydms.integration;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.junit.ClassRule;
import org.junit.Test;

import io.dropwizard.client.JerseyClientBuilder; 
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import net.binggl.mydms.MydmsApplication;
import net.binggl.mydms.MydmsConfiguration;

public class TagIntegrationTest {
	
	
//	private static final String TMP_FILE = createTempFile();
//    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test-example.yml");
//
//    @ClassRule
//    public static final DropwizardAppRule<HelloWorldConfiguration> RULE = new DropwizardAppRule<>(
//            HelloWorldApplication.class, CONFIG_PATH,
//            ConfigOverride.config("database.url", "jdbc:h2:" + TMP_FILE));
//
//    private Client client;
//
//    @BeforeClass
//    public static void migrateDb() throws Exception {
//        RULE.getApplication().run("db", "migrate", CONFIG_PATH);
//    }
//
//    @Before
//    public void setUp() throws Exception {
//        client = ClientBuilder.newClient();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        client.close();
//    }
//
//    private static String createTempFile() {
//        try {
//            return File.createTempFile("test-example", null).getAbsolutePath();
//        } catch (IOException e) {
//            throw new IllegalStateException(e);
//        }
//    }
	
	
	
	@ClassRule
    public static final DropwizardAppRule<MydmsConfiguration> RULE =
            new DropwizardAppRule<MydmsConfiguration>(MydmsApplication.class, ResourceHelpers.resourceFilePath("testing.yml"));

    @Test
    public void loginHandlerRedirectsAfterPost() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");

        Response response = client.target(
                 String.format("http://localhost:%d/api/tags", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
    }
}
