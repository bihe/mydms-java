package net.binggl.mydms.features.records

import net.binggl.mydms.features.records.entity.DocumentEntity
import net.binggl.mydms.features.records.entity.SenderEntity
import net.binggl.mydms.features.records.entity.TagEntity
import net.binggl.mydms.features.records.model.OrderBy
import net.binggl.mydms.features.records.model.SortOrder
import net.binggl.mydms.features.records.repository.DocumentRepository
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.IntStream

@RunWith(SpringRunner::class)
@DataJpaTest
class DocumentRepositoryTest {

    @Autowired private lateinit var repository: DocumentRepository

    private val document: DocumentEntity
        get() {
            return DocumentEntity(id = "id", title = "document", fileName = "filename", alternativeId = UUID.randomUUID().toString(),
                    previewLink = "previewLink", amount = 0.0, created = LocalDateTime.parse("2018-01-01T00:00:00.000", fmt),
                    modified = null,
                    tagList = "",
                    senderList = "",
                    tags = emptySet(),
                    senders = emptySet())
        }

    @Test
    fun saveFindDelete() {
        Assert.assertNotNull(this.repository)

        val doc = this.document.copy(created = LocalDateTime.now())

        val savedDocument = this.repository.save(doc)
        Assert.assertEquals(doc.created, savedDocument.created)

        val findDocument = this.repository.findById("id")
        Assert.assertTrue(findDocument.isPresent)
        Assert.assertEquals(savedDocument, findDocument.get())

        val doc2 = this.document.copy(id = UUID.randomUUID().toString(), created = LocalDateTime.now())
        val savedDocument2 = this.repository.save(doc2)
        Assert.assertEquals(doc2.created, savedDocument2.created)

        val changedDocument2 = savedDocument2.copy(title = "CHANGED")
        val changedDocument2Result = this.repository.save(changedDocument2)
        Assert.assertEquals(changedDocument2.id, changedDocument2Result.id)
        Assert.assertEquals(changedDocument2.title, changedDocument2Result.title)
        Assert.assertEquals(changedDocument2.created, changedDocument2Result.created)

        val items = this.repository.findAll()
        Assert.assertTrue(items.count() == 2)

        this.repository.delete(savedDocument2)

        val itemsLeft = this.repository.findAll()
        Assert.assertTrue(itemsLeft.count() == 1)

    }

    @Test
    fun searchForDocuments() {

        // create 10 records
        for(i in IntStream.range(0,10)) {
            val doc = this.repository.save(this.document.copy(id = "document$i",
                    title = "DocumentEntity #$i",
                    alternativeId = UUID.randomUUID().toString(),
                    created = LocalDateTime.now(),
                    tags = setOf(TagEntity("tag$i")),
                    senders = setOf(SenderEntity("sender$i"))
                )
            )
            Assert.assertEquals("DocumentEntity #$i", doc.title)
        }

        // search for all records
        val allDocuments = this.repository.searchDocuments(title = Optional.empty(),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertEquals(10, allDocuments.totalEntries)
        Assert.assertEquals(10, allDocuments.documents.size)

        // check limit && skip

        val documentsOffset = this.repository.searchDocuments(title = Optional.empty(),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.of(5),
                skip = Optional.empty(),
                order = OrderBy("title", SortOrder.Ascending))

        Assert.assertEquals(10, documentsOffset.totalEntries)
        Assert.assertEquals(5, documentsOffset.documents.size)
        Assert.assertEquals("DocumentEntity #0", documentsOffset.documents[0].title)

        val documentsOffset1 = this.repository.searchDocuments(title = Optional.empty(),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.of(3),
                skip = Optional.of(10),
                order = OrderBy("title", SortOrder.Ascending))

        Assert.assertEquals(10, documentsOffset1.totalEntries)
        Assert.assertEquals(0, documentsOffset1.documents.size)

        val documentsOffset2 = this.repository.searchDocuments(title = Optional.empty(),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.of(8),
                order = OrderBy("title", SortOrder.Ascending))

        Assert.assertEquals(10, documentsOffset2.totalEntries)
        Assert.assertEquals(2, documentsOffset2.documents.size)
        Assert.assertEquals("DocumentEntity #8", documentsOffset2.documents[0].title)
        Assert.assertEquals("DocumentEntity #9", documentsOffset2.documents[1].title)


        // search for documents by title

        val documentsTitle = this.repository.searchDocuments(title = Optional.of("document"),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertEquals(10, documentsTitle.totalEntries)
        Assert.assertEquals(10, documentsTitle.documents.size)

        val documentsTitleSingle = this.repository.searchDocuments(title = Optional.of("DocumentEntity #0"),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertEquals(1, documentsTitleSingle.totalEntries)
        Assert.assertEquals(1, documentsTitleSingle.documents.size)

        // order by

        val documentTitleOrderBy = this.repository.searchDocuments(title = Optional.of("document"),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.of(5),
                skip = Optional.empty(),
                order = *arrayOf(
                        OrderBy("id", SortOrder.Descending),
                        OrderBy("created",SortOrder.Ascending)
                )
        )

        Assert.assertEquals(10, documentTitleOrderBy.totalEntries)
        Assert.assertEquals(5, documentTitleOrderBy.documents.size)
        Assert.assertEquals("DocumentEntity #9", documentTitleOrderBy.documents[0].title)

        // filter date

        val documentsByDate = this.repository.searchDocuments(title = Optional.empty(),
                dateFrom = Optional.of(LocalDateTime.now().plusDays(1)),
                dateUntil = Optional.empty(),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertEquals(0, documentsByDate.totalEntries)
        Assert.assertEquals(0, documentsByDate.documents.size)

        val documentsByDate1 = this.repository.searchDocuments(title = Optional.empty(),
                dateUntil = Optional.of(LocalDateTime.now().plusDays(1)),
                dateFrom = Optional.of(LocalDateTime.now().minusDays(1)),
                sender = Optional.empty(),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty(),
                order = *arrayOf(
                        OrderBy("created",SortOrder.Ascending)
                ))

        Assert.assertEquals(10, documentsByDate1.totalEntries)
        Assert.assertEquals(10, documentsByDate1.documents.size)
        Assert.assertEquals("DocumentEntity #0", documentsByDate1.documents[0].title)

        // query "joined tables" tags + senders

        val documentsByTag = this.repository.searchDocuments(title = Optional.of("document"),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.of("sender0"),
                tag = Optional.of("tag0"),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertEquals(1, documentsByTag.totalEntries)
        Assert.assertEquals(1, documentsByTag.documents.size)
        Assert.assertEquals("DocumentEntity #0", documentsByTag.documents[0].title)
        Assert.assertEquals("tag0", documentsByTag.documents[0].tags[0])
        Assert.assertEquals("sender0", documentsByTag.documents[0].senders[0])


        val documentsByTagNoResult = this.repository.searchDocuments(title = Optional.of("document"),
                dateFrom = Optional.empty(),
                dateUntil = Optional.empty(),
                sender = Optional.of("sender99"),
                tag = Optional.empty(),
                limit = Optional.empty(),
                skip = Optional.empty())

        Assert.assertEquals(0, documentsByTagNoResult.totalEntries)
        Assert.assertEquals(0, documentsByTagNoResult.documents.size)


        // combine all parameters

        val documentsSearchAll = this.repository.searchDocuments(
                title = Optional.of("document"),
                dateFrom = Optional.of(LocalDateTime.now().minusDays(1)),
                dateUntil = Optional.of(LocalDateTime.now().plusDays(1)),
                sender = Optional.of("sender1"),
                tag = Optional.of("tag1"),
                limit = Optional.of(1),
                skip = Optional.of(0),
                order = *arrayOf(
                        OrderBy("title", SortOrder.Ascending),
                        OrderBy("modified",SortOrder.Descending)
                ))

        Assert.assertEquals(1, documentsSearchAll.totalEntries)
        Assert.assertEquals(1, documentsSearchAll.documents.size)
        Assert.assertEquals("DocumentEntity #1", documentsSearchAll.documents[0].title)
        Assert.assertEquals("tag1", documentsSearchAll.documents[0].tags[0])
        Assert.assertEquals("sender1", documentsSearchAll.documents[0].senders[0])

    }


    companion object {
        private val fmt = DateTimeFormatter.ISO_DATE_TIME
    }
}