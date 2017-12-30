package net.binggl.mydms.features.senders

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository

@Repository
class SenderStore(private val jdbcT: NamedParameterJdbcTemplate) {

    fun findAll(): List<Sender> {
        return this.jdbcT.query("SELECT id,name FROM SENDERS", { rs, _ ->
            Sender(id = rs.getLong("id"), name = rs.getString("name"))
        })
    }

    fun search(name: String): List<Sender> {
        return this.jdbcT.query("SELECT id,name FROM SENDERS WHERE name LIKE :name",
                MapSqlParameterSource("name", "%$name%"), { rs, _ ->
            Sender(id = rs.getLong("id"), name = rs.getString("name"))
        })
    }

    fun save(tag: Sender): Sender {
        val holder = GeneratedKeyHolder()
        this.jdbcT.update("INSERT INTO SENDERS (name) VALUES(:name)",
                MapSqlParameterSource("name", tag.name),
                holder)
        return Sender(holder.key?.toLong(), tag.name)
    }
}