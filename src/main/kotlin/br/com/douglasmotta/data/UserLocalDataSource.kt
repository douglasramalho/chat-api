package br.com.douglasmotta.data

import br.com.douglasmotta.data.db.table.UserEntity
import br.com.douglasmotta.data.model.User

interface UserLocalDataSource {

    suspend fun getUsers(): List<User>

    suspend fun getUserBy(id: Int): UserEntity?

    suspend fun getUserByUsername(username: String): User?

    suspend fun insertUser(entity: UserEntity): Boolean
}