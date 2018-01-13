package net.binggl.mydms.features.records.data

import net.binggl.mydms.features.records.model.Tag
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository

@Repository
class TagStore(private val jdbcT: NamedParameterJdbcTemplate) {

    fun findAll(): List<Tag> {
        return this.jdbcT.query("SELECT id,name FROM TAGS", { rs, _ ->
            Tag(id = rs.getLong("id"), name = rs.getString("name"))
        })
    }

    fun search(name: String): List<Tag> {
        return this.jdbcT.query("SELECT id,name FROM TAGS WHERE name LIKE :name",
                MapSqlParameterSource("name", "%$name%"), { rs, _ ->
            Tag(id = rs.getLong("id"), name = rs.getString("name"))
        })
    }

    fun save(tag: Tag): Tag {
        val holder = GeneratedKeyHolder()
        this.jdbcT.update("INSERT INTO TAGS (name) VALUES(:name)",
                MapSqlParameterSource("name", tag.name),
                holder)
        return Tag(holder.key?.toLong(), tag.name)
    }
}