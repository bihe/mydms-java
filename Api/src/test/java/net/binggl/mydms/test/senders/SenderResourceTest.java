package net.binggl.mydms.test.senders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.senders.SenderResource;
import net.binggl.mydms.features.senders.SenderStore;

public class SenderResourceTest {

	private static final SenderStore store = mock(SenderStore.class);
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new SenderResource(store))
            .build();

    @Before
    public void setup() {
    	Sender[] searchSender = new Sender[] { new Sender(1, "sender1") };
    	Sender[] all = new Sender[] { new Sender(1, "sender1"), new Sender(2, "sender2") };
    	
    	
    	when(store.searchByName(eq(null))).thenReturn(Arrays.asList(all));
    	when(store.searchByName(eq(""))).thenReturn(Arrays.asList(all));
    	when(store.searchByName(eq("sender1"))).thenReturn(Arrays.asList(searchSender));
        when(store.findAll()).thenReturn(Arrays.asList(all));
    }

    @After
    public void tearDown(){
        // we have to reset the mock after each test because of the
        // @ClassRule, or use a @Rule as mentioned below.
        reset(store);
    }

    @Test
    public void testSearchAll() throws Exception {
    	
    	Object result = resources.client().target("/senders/").request().get(ArrayList.class);
    	String json = MAPPER.writeValueAsString(result);
    	
    	Sender[] allSenders = new Sender[] { new Sender(1, "sender1"), new Sender(2, "sender2") };
    	String expected = MAPPER.writeValueAsString(Arrays.asList(allSenders));
    	
        assertThat(json).isEqualTo(expected);
        verify(store).findAll();
    }
    
	@Test
    public void testSearchSenders() throws Exception {
    	
    	Object result = resources.client().target("/senders/search?name=sender1").request().get(ArrayList.class);
    	String json = MAPPER.writeValueAsString(result);
    	Sender[] senders = new Sender[] { new Sender(1, "sender1") };
    	String expected = MAPPER.writeValueAsString(Arrays.asList(senders));
        assertThat(json).isEqualTo(expected);
        verify(store).searchByName("sender1");
        
        result = resources.client().target("/senders/search?name=").request().get(ArrayList.class);
    	json = MAPPER.writeValueAsString(result);
    	Sender[] allSenders = new Sender[] { new Sender(1, "sender1"), new Sender(2, "sender2") };
    	expected = MAPPER.writeValueAsString(Arrays.asList(allSenders));
        assertThat(json).isEqualTo(expected);
        verify(store).findAll();
        
    }
}
