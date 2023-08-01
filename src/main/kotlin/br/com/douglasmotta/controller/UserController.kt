package br.com.douglasmotta.controller

import br.com.douglasmotta.data.UserLocalDataSource
import br.com.douglasmotta.data.db.table.toResponse
import br.com.douglasmotta.data.model.toResponse
import br.com.douglasmotta.data.response.UserResponse

class UserController(
    private val userLocalDataSource: UserLocalDataSource,
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
}