package br.com.douglasmotta.data

import br.com.douglasmotta.data.db.DbHelper
import br.com.douglasmotta.data.db.images
import br.com.douglasmotta.data.db.table.ImageEntity
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find

class ImageDbDataSourceImpl : ImageLocalDataSource {

    private val database = DbHelper.database()

    override fun insertImage(entity: ImageEntity): Boolean {
        return database.images.add(entity) > 0
    }

    override fun findImageBy(imageId: Int): ImageEntity? {
        return database.images.find { it.id eq imageId }
    }
}