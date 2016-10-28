package net.binggl.mydms.test.documents;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.UUID;

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
        final Date date = stamp.toDate();
        
    	final Document document = new Document(UUID.fromString("a08bee51-3b09-4761-bc93-6f2e10b4f366"), "document", "filename", "alternativeId", "previewLink", 0.0, date, date);

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/document.json"), Document.class));
        assertThat(MAPPER.writeValueAsString(document)).isEqualTo(expected);
    }
}
