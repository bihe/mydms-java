package net.binggl.mydms.features.documents

import net.binggl.mydms.features.documents.data.DocumentStore
import net.binggl.mydms.features.documents.models.Document
import net.binggl.mydms.features.documents.models.OrderBy
import net.binggl.mydms.features.documents.models.SortOrder
import net.binggl.mydms.infrastructure.error.MydmsException
import org.joda.time.format.ISODateTimeFormat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
class DocumentStoreTest {

    @Autowired lateinit private var store: DocumentStore

    private val document: Document
        get() {
            return Document(id = "id", title = "document", fileName = "filename", alternativeId = UUID.randomUUID().toString(),
                    previewLink = "previewLink", amount = 0.0, created = fmt.parseDateTime("2018-01-01T00:00:00.000+01:00").toDate(),
                    modified = null, tags = emptyList(),
                    senders = emptyList(), uploadFileToken = null)
        }


    @Transactional
    @Test(expected = MydmsException::class)
    fun findByInvalidId() {
        val notfound = this.store.findById(id = "")
    }

    @Transactional
    @Test
    fun saveFindDelete() {
        val doc = this.document.copy(created = Date())

        val savedDocument = this.store.save(doc)
        Assert.assertEquals(doc.created, savedDocument.created)

        val findDocument = this.store.findById("id")
        Assert.assertTrue(findDocument.isPresent)
        Assert.assertEquals(savedDocument, findDocument.get())

        val doc2 = this.document.copy(id = UUID.randomUUID().toString(), created = Date())
        val savedDocument2 = this.store.save(doc2)
        Assert.assertEquals(doc2.created, savedDocument2.created)

        val changedDocument2 = savedDocument2.copy(title = "CHANGED")
        val changedDocument2Result = this.store.save(changedDocument2)
        Assert.assertEquals(changedDocument2.id, changedDocument2Result.id)
        Assert.assertEquals(changedDocument2.title, changedDocument2Result.title)
        Assert.assertEquals(changedDocument2.created, changedDocument2Result.created)

        val items = this.store.findAllItems()
        Assert.assertTrue(items.size == 2)

        val orderedItems = this.store.findAllItems(OrderBy("created", sort = SortOrder.Descending),
                OrderBy(field = "title", sort = SortOrder.Ascending))
        Assert.assertTrue(orderedItems.size == 2)
        Assert.assertEquals("id", orderedItems[1].id)

        val result = this.store.delete(savedDocument2)
        Assert.assertTrue(result)

        val cannotDelete = this.store.delete(this.document.copy(id="1"))
        Assert.assertFalse(cannotDelete)

        val itemsLeft = this.store.findAllItems()
        Assert.assertTrue(itemsLeft.size == 1)

    }

    companion object {
        private val fmt =  ISODateTimeFormat.dateTimeParser()
    }
}