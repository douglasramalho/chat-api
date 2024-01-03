package br.com.douglasmotta.controller

import br.com.douglasmotta.data.ImageLocalDataSource
import br.com.douglasmotta.data.UserLocalDataSource
import br.com.douglasmotta.data.db.table.UserEntity
import br.com.douglasmotta.data.model.User
import br.com.douglasmotta.data.request.CreateUserRequest

class AuthController(
    private val userLocalDataSource: UserLocalDataSource,
    private val imageLocalDataSource: ImageLocalDataSource,
) {
    suspend fun getUserByUsername(username: String): User? {
        return userLocalDataSource.getUserByUsername(username)
    }

    suspend fun insertUser(request: CreateUserRequest, hash: String, salt: String): Boolean {
        val imageEntity = request.profilePictureId?.let {
            imageLocalDataSource.findImageBy(it)
        }

        val userEntity = UserEntity {
            this.username = request.username
            this.password = hash
            this.salt = salt
            this.firstName = request.firstName
            this.lastName = request.lastName
            this.profileImage = imageEntity
        }

        return userLocalDataSource.insertUser(userEntity)
    }
}