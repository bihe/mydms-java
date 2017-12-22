package net.binggl.mydms.features.tags

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
class TagStoreTest {

    @Autowired lateinit private var store: TagStore

    @Transactional
    @Test
    fun getAllTest() {
        val t1 = this.store.save(Tag("tag1"))
        Assert.assertTrue(t1.id ?: 0 > -1)
        this.store.save(Tag("tag2"))

        val result = this.store.findAll()
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("tag1", result[0].name)
    }

    @Transactional
    @Test
    fun searchTest() {
        this.store.save(Tag("tag1"))
        this.store.save(Tag("tag2"))

        var result = this.store.search("tag1")
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("tag1", result[0].name)

        result = this.store.search("tag2")
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("tag2", result[0].name)

        result = this.store.search("tag")
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("tag1", result[0].name)
        Assert.assertEquals("tag2", result[1].name)
    }
}