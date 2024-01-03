package br.com.douglasmotta.data

import br.com.douglasmotta.data.db.table.ImageEntity

interface ImageLocalDataSource {

    fun insertImage(entity: ImageEntity): Boolean

    fun findImageBy(imageId: Int): ImageEntity?
}