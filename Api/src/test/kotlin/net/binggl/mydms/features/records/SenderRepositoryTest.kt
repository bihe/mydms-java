package net.binggl.mydms.features.records

import net.binggl.mydms.features.records.entity.SenderEntity
import net.binggl.mydms.features.records.repository.SenderRepository
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringRunner::class)
@DataJpaTest
class SenderRepositoryTest {

    @Autowired lateinit private var repository: SenderRepository

    @Transactional
    @Test
    fun getAllTest() {
        val t1 = this.repository.save(SenderEntity("sender1"))
        Assert.assertTrue(t1.id ?: 0 > -1)
        this.repository.save(SenderEntity("sender2"))

        val result = this.repository.findAll().toList()
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("sender1", result[0].name)
    }

    @Transactional
    @Test
    fun searchTest() {
        this.repository.save(SenderEntity("sender1"))
        this.repository.save(SenderEntity("sender2"))

        var result = this.repository.findByNameContainingIgnoreCase("sender1")
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("sender1", result[0].name)

        result = this.repository.findByNameContainingIgnoreCase("sender2")
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("sender2", result[0].name)

        result = this.repository.findByNameContainingIgnoreCase("sender")
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("sender1", result[0].name)
        Assert.assertEquals("sender2", result[1].name)
    }
}