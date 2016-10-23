package net.binggl.mydms.test.senders;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import net.binggl.mydms.senders.Sender;

public class SenderModelTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws Exception {
        final Sender sender = new Sender(1, "sender1");

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/sender.json"), Sender.class));

        assertThat(MAPPER.writeValueAsString(sender)).isEqualTo(expected);
    }
    
    @Test
    public void deserializesFromJSON() throws Exception {
        final Sender sender = new Sender(1, "sender1");
        assertThat(MAPPER.readValue(fixture("fixtures/sender.json"), Sender.class))
                .isEqualTo(sender);
    }
}
