package net.binggl.mydms.test.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

// use the code from a dropwizard pull request
// slightly changed in dropwizard 1.1.0
// change the test once the 1.1.0 release is available
// https://github.com/dropwizard/dropwizard/pull/1594/files
import io.dropwizard.testing.junit.DAOTestRule;
import net.binggl.mydms.features.documents.DocumentStore;
import net.binggl.mydms.features.documents.models.Document;
import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.senders.SenderStore;
import net.binggl.mydms.features.shared.store.OrderBy;
import net.binggl.mydms.features.shared.store.SortOrder;
import net.binggl.mydms.features.tags.Tag;
import net.binggl.mydms.features.tags.TagStore;

public class DocumentStoreTest {

	@Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
        .addEntityClass(Sender.class)
        .addEntityClass(Tag.class)
        .addEntityClass(Document.class)
        .build();

    private DocumentStore documentStore;

    @Before
    public void setUp() throws Exception {
        documentStore = new DocumentStore(database.getSessionFactory());
    }
    
    @Test
    public void saveTag() {
    	
    	Document document = new Document("document", "filename", "alternativeId", "previewLink", 1.0);
    	
        Document saved = database.transaction(() -> {
            return documentStore.save(document);
        });

        assertEquals(document.getTitle(), saved.getTitle());
        
        Optional<Document> found = documentStore.findById(saved.getId());
        assertTrue("Retrieve document from store", found.isPresent());
        
        Document update = found.get();
        update.setTitle("update1");
        database.transaction(() -> {
            return documentStore.save(update);
        });
        
        found = documentStore.findById(saved.getId());
        assertTrue("Retrieve document from store", found.isPresent());
        
        assertEquals(update.getId(), found.get().getId());
        assertEquals(update.getTitle(), found.get().getTitle());
        
        Document d = found.get();
        
        // add tags
        Tag tag1 = new Tag("tag1");
        Tag tag2 = new Tag("tag2");
        
        d.getTags().add(tag1);
        d.getTags().add(tag2);
        
        
        // add a sender
        Sender sender1 = new Sender("sender1");
        d.getSenders().add(sender1);
                
        Document ref = database.transaction(() -> {
            return documentStore.save(d);
        });
        
        assertNotNull(ref);
        assertEquals(2, ref.getTags().size());
        assertEquals(1, ref.getSenders().size());
        
        found = documentStore.findById(d.getId());
        assertTrue("Retrieve document from store", found.isPresent());
        
        Document d1 = found.get();
        
        // remove a tag
        d1.getTags().clear();
        Tag tag3 = new Tag("tag3");
        d1.getTags().add(tag3);
        
        ref = database.transaction(() -> {
            return documentStore.save(d1);
        });
        
        assertNotNull(ref);
        assertEquals(1, ref.getTags().size());
    }
   
    
    @Test
    public void deleteDocument() {
    	Document document = new Document("document", "filename", "alternativeId", "previewLink", 1.0);
    	
        Document saved = database.transaction(() -> {
            return documentStore.save(document);
        });
        UUID id = saved.getId();
        
        assertTrue("Document got id", id != null);
        
        database.transaction(() -> {
            documentStore.delete(id);
            return null;
        });
        
        Optional<Document> found = documentStore.findById(id);
        assertFalse("Document deleted", found.isPresent());
    }
    
    @Test
    public void searchDocuments() {
		
		
    	database.transaction(() -> {
    		for (int i = 1; i < 11; i++) {
				Document document = new Document(String.format("document%d", i), "filename", "alternativeId", "previewLink", 1.0);
				document.getTags().add(new Tag(String.format("tag%d", i)));
				document.getSenders().add(new Sender(String.format("sender%d", i)));
				documentStore.save(document);
			}
    		return null;
    	});
    	
    	TagStore tagStore = new TagStore(database.getSessionFactory());
    	SenderStore senderStore = new SenderStore(database.getSessionFactory());
    	
    	List<Tag> findTags = tagStore.searchByName("tag1");
    	assertNotNull(findTags);
    	Tag tag1 = findTags.get(0);
    	assertNotNull(tag1);
    	
    	List<Sender> findSenders = senderStore.searchByName("sender5");
    	assertNotNull(findSenders);
    	Sender sender5 = findSenders.get(0);
    	assertNotNull(sender5);
    	
    	List<Document> all = documentStore.findAll();
    	assertNotNull(all);
    	
    	List<Document> search = documentStore.searchDocuments(Optional.empty(), Optional.of(tag1.getId()), Optional.empty(), 
    			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.size());
    	
    	search = documentStore.searchDocuments(Optional.empty(), Optional.of(-1L), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(0, search.size());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(10, search.size());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.of(tag1.getId()), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.size());
    	assertEquals("document1", search.get(0).getTitle());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getId()), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.size());
    	assertEquals("document5", search.get(0).getTitle());
    	
    	DateTime from = new DateTime();
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getId()), 
    			Optional.of(from.minusHours(1).toDate()), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.size());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getId()), 
    			Optional.empty(), Optional.of(from.plusMinutes(1).toDate()), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.size());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getId()), 
    			Optional.of(from.minusHours(1).toDate()), Optional.of(from.plusMinutes(1).toDate()), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.size());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getId()), 
    			Optional.of(from.plusHours(1).toDate()), Optional.of(from.plusMinutes(1).toDate()), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(0, search.size());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getId()), 
    			Optional.of(from.minusHours(1).toDate()), Optional.of(from.minusMinutes(1).toDate()), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(0, search.size());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.empty(), Optional.empty(), 
    			Optional.empty(), Optional.of(5), Optional.empty());
    	assertNotNull(search);
    	assertEquals(5, search.size());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.empty(), Optional.empty(), 
    			Optional.empty(), Optional.of(10), Optional.of(5));
    	assertNotNull(search);
    	assertEquals(5, search.size());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.empty(), Optional.empty(), 
    			Optional.empty(), Optional.of(1), Optional.of(1), new OrderBy("title", SortOrder.Ascending));
    	assertNotNull(search);
    	assertEquals(1, search.size());
    	assertEquals("document10", search.get(0).getTitle());
    }
    
    
    @Test(expected = ConstraintViolationException.class)
    public void testValidation() {
    	Document test = new Document("", "", "alternativeId", "previewLink", 1.0);
    	
        database.transaction(() -> {
            return documentStore.save(test);
        });
    }
}
