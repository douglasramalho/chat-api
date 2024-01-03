package br.com.douglasmotta.controller

import br.com.douglasmotta.data.ImageLocalDataSource
import br.com.douglasmotta.data.MediaStorageDataSource
import br.com.douglasmotta.data.UserLocalDataSource
import br.com.douglasmotta.data.db.table.ImageEntity
import br.com.douglasmotta.data.db.table.toResponse
import br.com.douglasmotta.data.model.toResponse
import br.com.douglasmotta.data.response.ImageResponse
import br.com.douglasmotta.data.response.UserResponse
import java.io.File

class UserController(
    private val userLocalDataSource: UserLocalDataSource,
    private val imageLocalDataSource: ImageLocalDataSource,
    private val mediaStorageDataSource: MediaStorageDataSource,
) {

    suspend fun getUsers(): List<UserResponse> {
        val users = userLocalDataSource.getUsers()

        return users.map {
            it.toResponse()
        }
    }

    suspend fun getUserBy(id: Int): UserResponse? {
        return userLocalDataSource.getUserBy(id)?.toResponse()
    }

    suspend fun uploadImage(file: File): ImageResponse {
        val url = mediaStorageDataSource.storeProfilePicture(file)
        val imageEntity = ImageEntity {
            this.name = file.nameWithoutExtension
            this.type = file.extension
            this.url = url
        }

        imageLocalDataSource.insertImage(imageEntity)

        return imageEntity.toResponse()
    }
}