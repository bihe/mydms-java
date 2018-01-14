package net.binggl.mydms.features.records

import net.binggl.mydms.features.records.repository.DocumentRepository
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@RunWith(SpringRunner::class)
@DataJpaTest
class DocumentRepositoryTest {

    @Autowired lateinit private var repository: DocumentRepository

    @Test
    fun checkRepository() {
        Assert.assertNotNull(this.repository)
        /*
        val allDocuments = this.repository.searchDocuments(tile = Optional.empty(),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertNotNull(allDocuments)
        */
    }
}