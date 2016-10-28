package net.binggl.mydms.test.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

// use the code from a dropwizard pull request
// slightly changed in dropwizard 1.1.0
// change the test once the 1.1.0 release is available
// https://github.com/dropwizard/dropwizard/pull/1594/files
import io.dropwizard.testing.junit.DAOTestRule;
import net.binggl.mydms.features.documents.Document;
import net.binggl.mydms.features.documents.DocumentStore;
import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.tags.Tag;

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
    
//    @Test
//    public void findTags() {
//    	database.transaction(() -> {
//    		for(int i=0;i<10;i++) {
//    			documentStore.save(new Tag(String.format("%s%d", "tag", i)));	
//    		}
//            return null;
//        });
//    	
//    	List<Tag> tags = documentStore.findAll();
//    	assertNotNull(tags);
//    	assertEquals(10, tags.size());
//    	
//    	tags = documentStore.searchByName("tag");
//    	assertNotNull(tags);
//    	assertEquals(10, tags.size());
//    	
//    	tags = documentStore.searchByName("tag0");
//    	assertNotNull(tags);
//    	assertEquals(1, tags.size());
//    	assertEquals("tag0", tags.get(0).getName());
//    	
//    	tags = documentStore.searchByName("abc");
//    	assertNotNull(tags);
//    	assertEquals(0, tags.size());
//    }
    
    @Test(expected = ConstraintViolationException.class)
    public void testValidation() {
    	Document test = new Document("", "", "alternativeId", "previewLink", 1.0);
    	
        database.transaction(() -> {
            return documentStore.save(test);
        });
    }
}
