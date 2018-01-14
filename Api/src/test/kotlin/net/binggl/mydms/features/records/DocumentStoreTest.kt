package net.binggl.mydms.features.records


/*

import net.binggl.mydms.features.records.repository.DocumentStore
import net.binggl.mydms.features.records.data.SenderStore
import net.binggl.mydms.features.records.data.TagStore
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class DocumentStoreTest {

    @Autowired lateinit private var store: DocumentStore
    @Autowired lateinit private var tagStore: TagStore
    @Autowired lateinit private var senderStore: SenderStore

    private val document: DocumentEntity
        get() {
            return DocumentEntity(id = "id", title = "document", fileName = "filename", alternativeId = UUID.randomUUID().toString(),
                    previewLink = "previewLink", amount = 0.0, created = fmt.parseDateTime("2018-01-01T00:00:00.000+01:00").toDate(),
                    modified = null, tagEntities = emptyList(),
                    senderEntities = emptyList(), uploadFileToken = null)
        }


    @Transactional
    @Test(expected = MydmsException::class)
    fun findByInvalidId() {
        this.store.findById(id = "")
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

    @Transactional
    @Test
    fun searchForDocuments() {

        // create 5 tagEntities
        for(i in IntStream.range(0, 5)) {
            this.tagStore.save(TagEntity(id = 1, name = "tag$i"))
        }

        // create 5 senderEntities
        for(i in IntStream.range(0, 5)) {
            this.senderStore.save(SenderEntity(id = 1, name = "tag$i"))
        }

        // create 10 records
        for(i in IntStream.range(0,10)) {
            val doc = this.store.save(this.document.copy(id = "document$i", title = "DocumentEntity #$i",
                    alternativeId = UUID.randomUUID().toString(), created = Date()))
            Assert.assertEquals("DocumentEntity #$i", doc.title)
        }

        // search for all records
        val allDocuments = this.store.searchDocuments(tile = Optional.empty(),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertEquals(10, allDocuments.totalEntries)
        Assert.assertEquals(10, allDocuments.documents.size)

        // check limit && skip
        val documentsOffset = this.store.searchDocuments(tile = Optional.empty(),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.of(5),
                skip = Optional.empty())

        Assert.assertEquals(10, documentsOffset.totalEntries)
        Assert.assertEquals(5, documentsOffset.documents.size)

        val documentsOffset1 = this.store.searchDocuments(tile = Optional.empty(),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.of(3),
                skip = Optional.of(10))

        Assert.assertEquals(10, documentsOffset1.totalEntries)
        Assert.assertEquals(0, documentsOffset1.documents.size)

        val documentsOffset2 = this.store.searchDocuments(tile = Optional.empty(),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.of(8))

        Assert.assertEquals(10, documentsOffset2.totalEntries)
        Assert.assertEquals(2, documentsOffset2.documents.size)

        val documentsTitle = this.store.searchDocuments(tile = Optional.of("document"),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertEquals(10, documentsTitle.totalEntries)
        Assert.assertEquals(10, documentsTitle.documents.size)

        val documentsTitleSingle = this.store.searchDocuments(tile = Optional.of("DocumentEntity #0"),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertEquals(1, documentsTitleSingle.totalEntries)
        Assert.assertEquals(1, documentsTitleSingle.documents.size)

        val documentTitleOrderBy = this.store.searchDocuments(tile = Optional.of("document"),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.of(5),
                skip = Optional.empty(),
                order = *arrayOf(
                        OrderBy("DOCUMENTS.id", SortOrder.Descending),
                        OrderBy("DOCUMENTS.created",SortOrder.Ascending)
                )
        )

        Assert.assertEquals(10, documentTitleOrderBy.totalEntries)
        Assert.assertEquals(5, documentTitleOrderBy.documents.size)
        Assert.assertEquals("DocumentEntity #9", documentTitleOrderBy.documents[0].title)

        val documentsByDate = this.store.searchDocuments(tile = Optional.empty(),
                dateFrom = Optional.of(DateTime().plusDays(1).toDate()),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertEquals(0, documentsByDate.totalEntries)
        Assert.assertEquals(0, documentsByDate.documents.size)

        val documentsByDate1 = this.store.searchDocuments(tile = Optional.empty(),
                dateUntil = Optional.of(DateTime().plusDays(1).toDate()),
                dateFrom = Optional.of(DateTime().minusDays(1).toDate()),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty(),
                order = *arrayOf(
                        OrderBy("DOCUMENTS.created",SortOrder.Ascending)
                ))

        Assert.assertEquals(10, documentsByDate1.totalEntries)
        Assert.assertEquals(10, documentsByDate1.documents.size)
        Assert.assertEquals("DocumentEntity #0", documentsByDate1.documents[0].title)
    }

    companion object {
        private val fmt =  ISODateTimeFormat.dateTimeParser()
    }
}

        */