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

	private static final TagStore dao = mock(TagStore.class);
	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
	
	
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TagResource(dao))
            .build();

	private final Tag[] tags = new Tag[] { new Tag(1, "tag1") };

    @Before
    public void setup() {
        when(dao.searchTags(eq("tag1"))).thenReturn(Arrays.asList(tags));
    }

    @After
    public void tearDown(){
        // we have to reset the mock after each test because of the
        // @ClassRule, or use a @Rule as mentioned below.
        reset(dao);
    }

	@Test
    public void testGetPerson() throws Exception {
    	
    	Object result = resources.client().target("/tags/search?name=tag1").request().get(ArrayList.class);
    	
    	String json = MAPPER.writeValueAsString(result);
    	String expected = MAPPER.writeValueAsString(tags);
    	
        assertThat(json).isEqualTo(expected);
        verify(dao).searchTags("tag1");
    }
}
