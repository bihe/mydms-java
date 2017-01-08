package net.binggl.mydms.test.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

// use the code from a dropwizard pull request
// slightly changed in dropwizard 1.1.0
// change the test once the 1.1.0 release is available
// https://github.com/dropwizard/dropwizard/pull/1594/files
import io.dropwizard.testing.junit.DAOTestRule;
import net.binggl.mydms.features.documents.models.Document;
import net.binggl.mydms.features.documents.models.DocumentsSenders;
import net.binggl.mydms.features.documents.models.DocumentsTags;
import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.tags.Tag;
import net.binggl.mydms.features.tags.TagStore;

public class TagStoreTest {

	@Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
    		.addEntityClass(Sender.class)
            .addEntityClass(Tag.class)
            .addEntityClass(Document.class)
            .addEntityClass(DocumentsTags.class)
            .addEntityClass(DocumentsSenders.class)
        .build();

    private TagStore tagStore;

    @Before
    public void setUp() throws Exception {
        tagStore = new TagStore(database.getSessionFactory());
    }
    
    @Test
    public void saveTag() {
    	Tag tagTest = new Tag("new_tag_test");
        Tag savedTag = database.transaction(() -> {
            return tagStore.save(tagTest);
        });

        assertTrue("Tag got id", savedTag.getId() > -1);
        assertEquals(tagTest.getName(), savedTag.getName());
        
        Optional<Tag> foundTag = tagStore.findById(savedTag.getId());
        assertTrue("Retrieve tag from store", foundTag.isPresent());
        
        Tag update = foundTag.get();
        update.setName("update1");
        database.transaction(() -> {
            return tagStore.save(update);
        });
        
        foundTag = tagStore.findById(savedTag.getId());
        assertTrue("Retrieve tag from store", foundTag.isPresent());
        
        assertEquals(update.getId(), foundTag.get().getId());
        assertEquals(update.getName(), foundTag.get().getName());
    }
   
    
    @Test
    public void deleteTag() {
    	Tag tagTest = new Tag("new_tag_test");
        Tag savedTag = database.transaction(() -> {
            return tagStore.save(tagTest);
        });
        long id = savedTag.getId();
        
        assertTrue("Tag got id", id > -1);
        
        database.transaction(() -> {
            tagStore.delete(savedTag);
            return null;
        });
        
        Optional<Tag> foundTag = tagStore.findById(id);
        assertFalse("Tag deleted", foundTag.isPresent());
    }
    
    @Test
    public void findTags() {
    	database.transaction(() -> {
    		for(int i=0;i<10;i++) {
    			tagStore.save(new Tag(String.format("%s%d", "tag", i)));	
    		}
            return null;
        });
    	
    	List<Tag> tags = tagStore.findAll();
    	assertNotNull(tags);
    	assertEquals(10, tags.size());
    	
    	tags = tagStore.searchByName("tag");
    	assertNotNull(tags);
    	assertEquals(10, tags.size());
    	
    	tags = tagStore.searchByName("tag0");
    	assertNotNull(tags);
    	assertEquals(1, tags.size());
    	assertEquals("tag0", tags.get(0).getName());
    	
    	tags = tagStore.searchByName("abc");
    	assertNotNull(tags);
    	assertEquals(0, tags.size());
    }
    
    @Test(expected = ConstraintViolationException.class)
    public void testValidation() {
    	Tag tagTest = new Tag(null);
        database.transaction(() -> {
            return tagStore.save(tagTest);
        });
    }
}
