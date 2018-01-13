package net.binggl.mydms.features.upload

import net.binggl.mydms.features.upload.entity.Upload
import net.binggl.mydms.features.upload.repository.UploadRepository
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
@DataJpaTest
class UploadRepositoryTest {

    @Autowired lateinit private var store: UploadRepository

    @Transactional
    @Test
    fun saveSearchDelete() {
        val uploadItem = Upload(id = "id", fileName = "fileName", mimeType = "mimeType")
        val savedUploadItem = this.store.save(uploadItem)
        Assert.assertTrue( LocalDateTime.now().isAfter(savedUploadItem.created))

        val changedUploadItem = savedUploadItem.copy(fileName = "fileName2")
        val updatedUploadItem = this.store.save(changedUploadItem)
        Assert.assertEquals(savedUploadItem.created, updatedUploadItem.created)

        val all = this.store.findAll().toList()
        Assert.assertEquals(1, all.size)

        val findUploadItem = this.store.findById("id")
        Assert.assertTrue(findUploadItem.isPresent)

        this.store.delete(uploadItem)
        val deletedUploadItem = this.store.findById("id")
        Assert.assertTrue(!deletedUploadItem.isPresent)

        val findAll = this.store.findAll().toList()
        Assert.assertEquals(0, findAll.size)
    }
}