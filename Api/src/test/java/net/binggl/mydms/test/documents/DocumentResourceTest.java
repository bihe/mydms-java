package net.binggl.mydms.test.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import io.dropwizard.testing.junit.ResourceTestRule;
import net.binggl.mydms.features.documents.DocumentResource;
import net.binggl.mydms.features.documents.DocumentStore;
import net.binggl.mydms.features.documents.models.Document;
import net.binggl.mydms.features.shared.store.OrderBy;
import net.binggl.mydms.features.shared.store.SortOrder;

public class DocumentResourceTest {

	private static final DocumentStore store = mock(DocumentStore.class);
	
	private OrderBy[] SORT_ORDER = new OrderBy[] {
			new OrderBy("title", SortOrder.Ascending),
			new OrderBy("created", SortOrder.Ascending)
	};
	
    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new DocumentResource(store, null, null, null, null, null))
            .build();

    @Before
    public void setup() {
    	Document[] all = new Document[] { 
			new Document("document1", "filename", "alternativeId", "previewLink", 1.0),
			new Document("document2", "filename", "alternativeId", "previewLink", 1.0),
			new Document("document3", "filename", "alternativeId", "previewLink", 1.0)
    	};
        when(store.findAll(SORT_ORDER)).thenReturn(Arrays.asList(all));
    }

    @After
    public void tearDown(){
        // we have to reset the mock after each test because of the
        // @ClassRule, or use a @Rule as mentioned below.
        reset(store);
    }

    @Test
    public void testSearchAll() throws Exception {
    	
    	@SuppressWarnings("unchecked")
		List<Document> docs = resources.client().target("/api/documents/").request().get(ArrayList.class);
    	assertNotNull(docs);
    	assertEquals(3, docs.size());
    	
        verify(store).findAll(SORT_ORDER);
    }
}
