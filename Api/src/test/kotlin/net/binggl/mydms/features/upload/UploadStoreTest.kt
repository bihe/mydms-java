package net.binggl.mydms.features.upload

import net.binggl.mydms.features.upload.data.UploadStore
import net.binggl.mydms.features.upload.models.UploadItem
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
class UploadStoreTest {

    @Autowired lateinit private var store: UploadStore

    @Transactional
    @Test
    fun saveSearchDelete() {
        val uploadItem = UploadItem(id = "id", fileName = "fileName", mimeType = "mimeType", created = null)
        val savedUploadItem = this.store.save(uploadItem)
        Assert.assertTrue(savedUploadItem.created != null)

        val changedUploadItem = savedUploadItem.copy(fileName = "fileName2")
        val updatedUploadItem = this.store.save(changedUploadItem)
        Assert.assertEquals(savedUploadItem.created, updatedUploadItem.created)

        val all = this.store.findAll()
        Assert.assertEquals(1, all.size)

        val findUploadItem = this.store.findById("id")
        Assert.assertTrue(findUploadItem.isPresent)

        this.store.delete(uploadItem)
        val deletedUploadItem = this.store.findById("id")
        Assert.assertTrue(!deletedUploadItem.isPresent)

        val findAll = this.store.findAll()
        Assert.assertEquals(0, findAll.size)
    }
}