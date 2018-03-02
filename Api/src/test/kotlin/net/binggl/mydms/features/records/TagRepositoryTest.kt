package net.binggl.mydms.features.records

import net.binggl.mydms.features.records.entity.TagEntity
import net.binggl.mydms.features.records.repository.TagRepository
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringRunner::class)
@DataJpaTest
class TagRepositoryTest {

    @Autowired lateinit private var repository: TagRepository

    @Transactional
    @Test
    fun getAllTest() {
        val t1 = this.repository.save(TagEntity("tag1"))
        Assert.assertTrue(t1.id ?: 0 > -1)
        this.repository.save(TagEntity("tag2"))

        val result = this.repository.findAll().toList()
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("tag1", result[0].name)
    }

    @Transactional
    @Test
    fun searchTest() {
        this.repository.save(TagEntity("tag1"))
        this.repository.save(TagEntity("tag2"))

        var result = this.repository.findByNameContainingIgnoreCase("tag1")
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("tag1", result[0].name)

        result = this.repository.findByNameContainingIgnoreCase("tag2")
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("tag2", result[0].name)

        result = this.repository.findByNameContainingIgnoreCase("tag")
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("tag1", result[0].name)
        Assert.assertEquals("tag2", result[1].name)
    }
}