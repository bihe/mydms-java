package net.binggl.mydms.test.upload;

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
import net.binggl.mydms.features.upload.UploadStore;
import net.binggl.mydms.features.upload.models.UploadItem;

public class UploadStoreTest {

	@Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
        .addEntityClass(UploadItem.class)
        .build();

    private UploadStore store;

    @Before
    public void setUp() throws Exception {
    	store = new UploadStore(database.getSessionFactory());
    }
    
    @Test
    public void saveSender() {
    	UUID id = UUID.randomUUID();
    	UploadItem item = new UploadItem(id, "test.pdf", "application/pdf");
        UploadItem saved = database.transaction(() -> {
            return store.save(item);
        });
        assertNotNull(saved);
        assertEquals(item.getFileName(), saved.getFileName());
        
        Optional<UploadItem> found = store.findById(id);
        assertTrue("Retrieve item from store", found.isPresent());
        
        UploadItem update = found.get();
        update.setMimeType("application/octet-stream");
        database.transaction(() -> {
            return store.save(update);
        });
        
        found = store.findById(id);
        assertTrue("Retrieve item from store", found.isPresent());
        
        assertEquals(update.getId(), found.get().getId());
        assertEquals(update.getMimeType(), found.get().getMimeType());
    }
   
    
    @Test
    public void deleteSender() {
    	UUID id = UUID.randomUUID();
    	UploadItem item = new UploadItem(id, "test.pdf", "application/pdf");
    	UploadItem saved = database.transaction(() -> {
            return store.save(item);
        });
        
        database.transaction(() -> {
            store.delete(saved);
            return null;
        });
        
        Optional<UploadItem> found = store.findById(id);
        assertFalse("Sender deleted", found.isPresent());
    }
    
    
    
    @Test(expected = ConstraintViolationException.class)
    public void testValidation() {
    	UUID id = UUID.randomUUID();
    	UploadItem item = new UploadItem(id, null, null);
        database.transaction(() -> {
            return store.save(item);
        });
    }
}
