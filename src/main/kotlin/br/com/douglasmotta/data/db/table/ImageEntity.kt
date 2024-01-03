package br.com.douglasmotta.data.db.table

import br.com.douglasmotta.data.response.ImageResponse
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface ImageEntity : Entity<ImageEntity> {
    companion object : Entity.Factory<ImageEntity>()
    val id: Int
    var name: String
    var type: String
    var url: String
}

object Images: Table<ImageEntity>("images") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val type = varchar("type").bindTo { it.type }
    val url = varchar("url").bindTo { it.url }
}

fun ImageEntity.toResponse() = ImageResponse(
    id = this.id,
    name = this.name,
    type = this.type,
    url = this.url,
)