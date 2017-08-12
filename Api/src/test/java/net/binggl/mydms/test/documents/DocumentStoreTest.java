package net.binggl.mydms.test.documents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import net.binggl.mydms.features.documents.viewmodels.PagedDocuments;
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
import net.binggl.mydms.features.documents.models.DocumentsSenders;
import net.binggl.mydms.features.documents.models.DocumentsTags;
import net.binggl.mydms.features.documents.viewmodels.DocumentViewModel;
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
        .addEntityClass(DocumentsTags.class)
        .addEntityClass(DocumentsSenders.class)
//        .setConnectionDriverClass(org.h2.Driver.class)
//        .setConnectionUrl("jdbc:h2:/tmp/mydms.integration")
//        .setConnectionUsername("sa")
        .build();

    private DocumentStore documentStore;
    private TagStore tagStore;
    private SenderStore senderStore;

    @Before
    public void setUp() throws Exception {
        documentStore = new DocumentStore(database.getSessionFactory());
        tagStore = new TagStore(database.getSessionFactory());
        senderStore = new SenderStore(database.getSessionFactory());
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
        
        DocumentsTags tagRef1 = new DocumentsTags(d, tag1);
        DocumentsTags tagRef2 = new DocumentsTags(d, tag2);
        
        d.getTags().add(tagRef1);
        d.getTags().add(tagRef2);
        
        // add a sender
        Sender sender1 = new Sender("sender1");
                
        DocumentsSenders senderRef1 = new DocumentsSenders(d, sender1);
        
        d.getSenders().add(senderRef1);
        
        Document ref = database.transaction(() -> {
        	
        	senderStore.save(sender1);
        	
        	tagStore.save(tag1);
        	tagStore.save(tag2);
        	
        	Document r = documentStore.save(d);
        	return r;
        });
        
        assertNotNull(ref);
        assertEquals(2, ref.getTags().size());
        assertEquals(1, ref.getSenders().size());
        
        found = documentStore.findById(d.getId());
        assertTrue("Retrieve document from store", found.isPresent());
        
        Document d1 = found.get();
        
        // remove a tag
        d1.getTags().clear();
        
        ref = database.transaction(() -> {
        	documentStore.flush();
        	
        	Tag tag3 = new Tag("tag3");
            DocumentsTags tagRef3 = new DocumentsTags(d1, tag3);
            DocumentsTags tagRef4 = new DocumentsTags(d1, tag1);
                    
            d1.getTags().add(tagRef3);
            d1.getTags().add(tagRef4);
            
            tagStore.save(tag3);
        	
            Document doc = documentStore.save(d1);
            
        	return doc;
        });
        
        
        assertNotNull(ref);
        assertEquals(2, ref.getTags().size());
        
    }
   
    
    @Test
    public void deleteDocument() {
    	Document document = new Document("document", "filename", "alternativeId", "previewLink", 1.0);
    	
        Document saved = database.transaction(() -> {
            return documentStore.save(document);
        });
        String id = saved.getId();
        
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
				Document document = new Document(String.format("document%d", i), "filename", String.format("alternativeId%d", i), "previewLink", 1.0);
				
				Tag tag = new Tag(String.format("tag%d", i));
				DocumentsTags tagRef = new DocumentsTags(document, tag);
				document.getTags().add(tagRef);
				tagStore.save(tag);
				
				Sender sender = new Sender(String.format("sender%d", i));
				DocumentsSenders senderRef = new DocumentsSenders(document, sender);
				document.getSenders().add(senderRef);
				senderStore.save(sender);				
				
				documentStore.save(document);
			}
    		return null;
    	});
    	
    	    	
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
    	
    	PagedDocuments search = documentStore.searchDocuments(Optional.empty(), Optional.of(tag1.getName()), Optional.empty(),
    			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.getEntries());
    	
    	search = documentStore.searchDocuments(Optional.empty(), Optional.of("__"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(0, search.getEntries());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(10, search.getEntries());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.of(tag1.getName()), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.getEntries());
    	assertEquals("document1", search.getDocuments().get(0).getTitle());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getName()), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.getEntries());
    	assertEquals("document5", search.getDocuments().get(0).getTitle());
    	
    	DateTime from = new DateTime();
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getName()), 
    			Optional.of(from.minusHours(1).toDate()), Optional.empty(), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.getEntries());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getName()), 
    			Optional.empty(), Optional.of(from.plusMinutes(1).toDate()), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.getEntries());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getName()), 
    			Optional.of(from.minusHours(1).toDate()), Optional.of(from.plusMinutes(1).toDate()), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(1, search.getEntries());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getName()), 
    			Optional.of(from.plusHours(1).toDate()), Optional.of(from.plusMinutes(1).toDate()), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(0, search.getEntries());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.of(sender5.getName()), 
    			Optional.of(from.minusHours(1).toDate()), Optional.of(from.minusMinutes(1).toDate()), Optional.empty(), Optional.empty());
    	assertNotNull(search);
    	assertEquals(0, search.getEntries());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.empty(), Optional.empty(), 
    			Optional.empty(), Optional.of(5), Optional.empty());
    	assertNotNull(search);
    	assertEquals(5, search.getEntries());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.empty(), Optional.empty(), 
    			Optional.empty(), Optional.of(10), Optional.of(5));
    	assertNotNull(search);
    	assertEquals(5, search.getEntries());
    	
    	search = documentStore.searchDocuments(Optional.of("DOCUMENT"), Optional.empty(), Optional.empty(), Optional.empty(), 
    			Optional.empty(), Optional.of(1), Optional.of(1), new OrderBy("title", SortOrder.Ascending));
    	assertNotNull(search);
    	assertEquals(1, search.getEntries());
    	assertEquals("document10", search.getDocuments().get(0).getTitle());
    }
    
    
    @Test(expected = ConstraintViolationException.class)
    public void testValidation() {
    	Document test = new Document("", "", "alternativeId", "previewLink", 1.0);
    	
        database.transaction(() -> {
            return documentStore.save(test);
        });
    }
}
