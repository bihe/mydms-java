package net.binggl.mydms.features.upload.data

import net.binggl.mydms.features.upload.models.UploadItem
import net.binggl.mydms.infrastructure.error.MydmsException
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JdbcUploadStore(private val jdbcT: NamedParameterJdbcTemplate) : UploadStore {

    override fun findAll(): List<UploadItem> {
        return this.jdbcT.query("SELECT id,filename,mimetype,created FROM UPLOADS", { rs, _ ->
            UploadItem(id = rs.getString("id"),
                    fileName = rs.getString("filename"),
                    mimeType = rs.getString("mimetype"),
                    created = rs.getDate("created"))
        })
    }

    override fun findById(token: String): Optional<UploadItem> {
        val result = this.jdbcT.query("SELECT id,filename,mimetype,created FROM UPLOADS WHERE id = :token",
                MapSqlParameterSource("token", token), { rs, _ ->
            UploadItem(id = rs.getString("id"),
                    fileName = rs.getString("filename"),
                    mimeType = rs.getString("mimetype"),
                    created = rs.getDate("created"))
        })

        if (result.isEmpty()) {
            return Optional.empty()
        }
        return Optional.of(result[0])
    }

    override fun save(uploadItem: UploadItem): UploadItem {
        if (StringUtils.isEmpty(uploadItem.id)) {
            throw MydmsException("The upload-ID has to be assigned to save the element!")
        }

        var operationCount: Int

        val item = this.findById(uploadItem.id)
        if (item.isPresent) {
            operationCount = this.jdbcT.update("UPDATE UPLOADS SET filename=:filename, mimetype=:mimetype",
                    MapSqlParameterSource()
                            .addValue("filename", uploadItem.fileName)
                            .addValue("mimetype", uploadItem.mimeType)
                    )
        } else {
            operationCount = this.jdbcT.update("INSERT INTO UPLOADS (id,filename,mimetype) VALUES(:id,:filename,:mimetype)",
                    MapSqlParameterSource()
                            .addValue("id", uploadItem.id)
                            .addValue("filename", uploadItem.fileName)
                            .addValue("mimetype", uploadItem.mimeType)
            )
        }

        if (operationCount == 1) {
            val update = this.findById(uploadItem.id)
            if (update.isPresent) {
                return update.get()
            }
        }
        throw MydmsException("Could not retrieve updated item!")
    }

    override fun delete(uploadItem: UploadItem) {
        if (StringUtils.isEmpty(uploadItem.id)) {
            throw MydmsException("The ID has to be present!")
        }

        try {

            val item = this.findById(uploadItem.id)
            if (item.isPresent) {
                val operationCount = this.jdbcT.update("DELETE FROM UPLOADS WHERE id = :id",
                        MapSqlParameterSource().addValue("id", uploadItem.id))

                if (operationCount != 1) {
                    LOG.warn("The result of the delete-operation is: $operationCount")
                }
            }

        } catch (dataEx: DataAccessException) {
            LOG.error("Could not delete upload item: ${dataEx.message}")
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(JdbcUploadStore::class.java)
    }

}