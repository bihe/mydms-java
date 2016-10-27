package net.binggl.mydms.test.senders;

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
import net.binggl.mydms.features.senders.Sender;
import net.binggl.mydms.features.senders.SenderStore;

public class SenderStoreTest {

	@Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
        .addEntityClass(Sender.class)
        .build();

    private SenderStore senderStore;

    @Before
    public void setUp() throws Exception {
        senderStore = new SenderStore(database.getSessionFactory());
    }
    
    @Test
    public void saveSender() {
    	Sender senderTest = new Sender("new_sender_test");
        Sender savedSender = database.transaction(() -> {
            return senderStore.save(senderTest);
        });

        assertTrue("Sender got id", savedSender.getId() > -1);
        assertEquals(senderTest.getName(), savedSender.getName());
        
        Optional<Sender> foundSender = senderStore.findById(savedSender.getId());
        assertTrue("Retrieve sender from store", foundSender.isPresent());
        
        Sender update = foundSender.get();
        update.setName("update1");
        database.transaction(() -> {
            return senderStore.save(update);
        });
        
        foundSender = senderStore.findById(savedSender.getId());
        assertTrue("Retrieve sender from store", foundSender.isPresent());
        
        assertEquals(update.getId(), foundSender.get().getId());
        assertEquals(update.getName(), foundSender.get().getName());
    }
   
    
    @Test
    public void deleteSender() {
    	Sender senderTest = new Sender("new_sender_test");
        Sender savedSender = database.transaction(() -> {
            return senderStore.save(senderTest);
        });
        long id = savedSender.getId();
        
        assertTrue("Sender got id", id > -1);
        
        database.transaction(() -> {
            senderStore.delete(savedSender);
            return null;
        });
        
        Optional<Sender> foundSender = senderStore.findById(id);
        assertFalse("Sender deleted", foundSender.isPresent());
    }
    
    @Test
    public void findSenders() {
    	database.transaction(() -> {
    		for(int i=0;i<10;i++) {
    			senderStore.save(new Sender(String.format("%s%d", "sender", i)));	
    		}
            return null;
        });
    	
    	List<Sender> senders = senderStore.findAll();
    	assertNotNull(senders);
    	assertEquals(10, senders.size());
    	
    	senders = senderStore.searchByName("sender");
    	assertNotNull(senders);
    	assertEquals(10, senders.size());
    	
    	senders = senderStore.searchByName("sender0");
    	assertNotNull(senders);
    	assertEquals(1, senders.size());
    	assertEquals("sender0", senders.get(0).getName());
    	
    	senders = senderStore.searchByName("abc");
    	assertNotNull(senders);
    	assertEquals(0, senders.size());
    }
    
    @Test(expected = ConstraintViolationException.class)
    public void testValidation() {
    	Sender senderTest = new Sender(null);
        database.transaction(() -> {
            return senderStore.save(senderTest);
        });
    }
}
