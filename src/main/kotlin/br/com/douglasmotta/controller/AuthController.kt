package br.com.douglasmotta.controller

import br.com.douglasmotta.data.UserLocalDataSource
import br.com.douglasmotta.data.db.table.UserEntity
import br.com.douglasmotta.data.model.User
import br.com.douglasmotta.data.request.CreateUserRequest

class AuthController(
    private val userLocalDataSource: UserLocalDataSource,
) {
    suspend fun getUserByUsername(username: String): User? {
        return userLocalDataSource.getUserByUsername(username)
    }

    suspend fun insertUser(request: CreateUserRequest, hash: String, salt: String): Boolean {
        val userEntity = UserEntity {
            this.username = request.username
            this.password = hash
            this.salt = salt
            this.firstName = request.firstName
            this.lastName = request.lastName
            this.profilePictureUrl = request.profilePictureUrl
        }

        return userLocalDataSource.insertUser(userEntity)
    }
}