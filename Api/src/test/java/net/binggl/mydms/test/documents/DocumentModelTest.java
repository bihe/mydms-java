package net.binggl.mydms.test.documents;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import net.binggl.mydms.features.documents.Document;

public class DocumentModelTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
    @Test
    public void serializesAndDeserializeJSON() throws Exception {
        final DateTime stamp = new DateTime(2016,10,1,0,0,1,1, DateTimeZone.UTC);
        
    	final Document document = new Document(1, "document", "filename", "alternativeId", "previewLink", 0.0, stamp, stamp);

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/document.json"), Document.class));
        assertThat(MAPPER.writeValueAsString(document)).isEqualTo(expected);
    }
}
