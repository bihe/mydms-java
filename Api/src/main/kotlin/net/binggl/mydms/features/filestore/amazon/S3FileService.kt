package net.binggl.mydms.features.filestore.amazon

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import net.binggl.mydms.features.filestore.FileService
import net.binggl.mydms.features.filestore.model.FileItem
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.util.*

@Service
class S3FileService(@Value("\${aws.accessKey}") private val accessKey: String,
                    @Value("\${aws.secretKey}") private val secretKey: String,
                    @Value("\${aws.bucketName}") private val bucketName: String
                    ) : FileService {


    private lateinit var s3client: AmazonS3

    init {
        val credentials = BasicAWSCredentials(
                this.accessKey,
                this.secretKey
        )
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_CENTRAL_1) // no need to configure this one!
                .build()
    }

    /**
     * use the given file and store it in the S3 backend
     */
    override fun saveFile(file: FileItem): Boolean {
        var result = false
        val storagePath = "${file.folderName}/${file.fileName}"
        try {

            LOG.debug("Try to upload document to $storagePath.")

            val meta = ObjectMetadata()
            meta.contentEncoding = "UTF-8"
            meta.contentType = file.mimeType
            meta.contentLength = file.payload.size.toLong()

            var s3result = this.s3client.putObject(
                    this.bucketName,
                    storagePath,
                    ByteArrayInputStream(file.payload.toByteArray()),
                    meta)

            LOG.info("Upload operation result. eTag: ${s3result.eTag}")
            result = true

        } catch (awsEx: AmazonServiceException) {
            LOG.info("Could not upload object $storagePath. Reason: ${awsEx.message}")
        } catch (awsClientEx: AmazonClientException) {
            LOG.error("Error during interaction with S3: ${awsClientEx.message}", awsClientEx)
        }
        return result
    }

    /**
     * retrieve a item from the backend store
     */
    override fun getFile(filePath: String): Optional<FileItem> {
        var file = Optional.empty<FileItem>()
        try {
            LOG.debug("Try to get S3 resource from bucket: ${this.bucketName} using path $filePath.")

            var fileUrlPath = filePath
            if (fileUrlPath.startsWith("/")) {
                fileUrlPath = fileUrlPath.substring(1, fileUrlPath.length)
            }

            val parts = fileUrlPath.split("/")
            val path = parts[0]
            val fileName = parts[1]

            val s3object = this.s3client.getObject(bucketName, fileUrlPath)
            val inputStream = s3object.objectContent

            val fileObject = FileItem(fileName = fileName, folderName = path,
                    mimeType = s3object.objectMetadata.contentType,
                    payload = IOUtils.toByteArray(inputStream).toTypedArray())

            LOG.debug("Got object $fileObject")

            file = Optional.of(fileObject)

        } catch (awsEx: AmazonServiceException) {
            LOG.info("Could not get object $filePath. Reason: ${awsEx.message}")
        } catch (awsClientEx: AmazonClientException) {
            LOG.error("Error during interaction with S3: ${awsClientEx.message}", awsClientEx)
        }
        return file
    }


    companion object {
        private val LOG = LoggerFactory.getLogger(S3FileService::class.java)
    }
}

