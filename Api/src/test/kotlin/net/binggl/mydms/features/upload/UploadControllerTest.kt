package net.binggl.mydms.features.upload

import net.binggl.mydms.features.upload.data.UploadStore
import net.binggl.mydms.testinfrastructure.BaseIntegrationTest
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.nio.charset.StandardCharsets

@RunWith(SpringRunner::class)
@WebMvcTest(UploadController::class)
class UploadControllerTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var store: UploadStore

    @MockBean
    private lateinit var config: UploadConfig

    @Test
    fun testUpload() {
        Assert.assertTrue(true)

        val fileContent = "bar".toByteArray(StandardCharsets.UTF_8)
        val filePart = MockMultipartFile("file", "test.pdf", "application/pdf", fileContent)

        given(this.config.maxUploadSize).willReturn(Long.MAX_VALUE)
        given(this.config.uploadPath).willReturn("./target")
        given(this.config.allowedFileTypes).willReturn(listOf("application/pdf"))

        this.mvc.perform(
                MockMvcRequestBuilders.multipart("/api/v1/upload/file")
                        .file(filePart))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().encoding("UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString("File test.pdf was uploaded and stored using token")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("Created"))
    }
}