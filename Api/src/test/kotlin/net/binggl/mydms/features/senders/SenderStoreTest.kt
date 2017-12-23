package net.binggl.mydms.features.senders

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
class SenderStoreTest {

    @Autowired lateinit private var store: SenderStore

    @Transactional
    @Test
    fun getAllTest() {
        val t1 = this.store.save(Sender("sender1"))
        Assert.assertTrue(t1.id ?: 0 > -1)
        this.store.save(Sender("sender2"))

        val result = this.store.findAll()
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("sender1", result[0].name)
    }

    @Transactional
    @Test
    fun searchTest() {
        this.store.save(Sender("sender1"))
        this.store.save(Sender("sender2"))

        var result = this.store.search("sender1")
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("sender1", result[0].name)

        result = this.store.search("sender2")
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("sender2", result[0].name)

        result = this.store.search("sender")
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("sender1", result[0].name)
        Assert.assertEquals("sender2", result[1].name)
    }
}