package net.binggl.mydms.test.tags;

import static io.dropwizard.testing.FixtureHelpers.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import net.binggl.mydms.tags.Tag;

public class TagModelTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws Exception {
        final Tag tag = new Tag(1, "tag1");

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/tag.json"), Tag.class));

        assertThat(MAPPER.writeValueAsString(tag)).isEqualTo(expected);
    }
    
    @Test
    public void deserializesFromJSON() throws Exception {
        final Tag tag = new Tag(1, "tag1");
        assertThat(MAPPER.readValue(fixture("fixtures/tag.json"), Tag.class))
                .isEqualTo(tag);
    }
}
