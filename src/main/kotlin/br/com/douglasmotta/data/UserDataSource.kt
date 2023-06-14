package br.com.douglasmotta.data

import br.com.douglasmotta.data.model.User

interface UserDataSource {

    suspend fun getUserBy(id: String): User?

    suspend fun getUserByUsername(username: String): User?

    suspend fun insertUser(user: User): Boolean
}