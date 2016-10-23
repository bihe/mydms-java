package net.binggl.mydms.test.tags;

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
import net.binggl.mydms.tags.Tag;
import net.binggl.mydms.tags.TagResource;
import net.binggl.mydms.tags.TagStore;

public class TagResourceTest {

	private static final TagStore store = mock(TagStore.class);
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TagResource(store))
            .build();

    @Before
    public void setup() {
    	Tag[] searchTag = new Tag[] { new Tag(1, "tag1") };
    	Tag[] all = new Tag[] { new Tag(1, "tag1"), new Tag(2, "tag2") };
    	
    	
    	when(store.searchTags(eq(null))).thenReturn(Arrays.asList(all));
    	when(store.searchTags(eq(""))).thenReturn(Arrays.asList(all));
    	when(store.searchTags(eq("tag1"))).thenReturn(Arrays.asList(searchTag));
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
    	
    	Object result = resources.client().target("/tags/").request().get(ArrayList.class);
    	String json = MAPPER.writeValueAsString(result);
    	
    	Tag[] allTags = new Tag[] { new Tag(1, "tag1"), new Tag(2, "tag2") };
    	String expected = MAPPER.writeValueAsString(Arrays.asList(allTags));
    	
        assertThat(json).isEqualTo(expected);
        verify(store).findAll();
    }
    
	@Test
    public void testSearchTags() throws Exception {
    	
    	Object result = resources.client().target("/tags/search?name=tag1").request().get(ArrayList.class);
    	String json = MAPPER.writeValueAsString(result);
    	Tag[] tags = new Tag[] { new Tag(1, "tag1") };
    	String expected = MAPPER.writeValueAsString(Arrays.asList(tags));
        assertThat(json).isEqualTo(expected);
        verify(store).searchTags("tag1");
        
        result = resources.client().target("/tags/search?name=").request().get(ArrayList.class);
    	json = MAPPER.writeValueAsString(result);
    	Tag[] allTags = new Tag[] { new Tag(1, "tag1"), new Tag(2, "tag2") };
    	expected = MAPPER.writeValueAsString(Arrays.asList(allTags));
        assertThat(json).isEqualTo(expected);
        verify(store).findAll();
        
    }
}
