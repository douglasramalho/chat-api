package br.com.douglasmotta.data

import br.com.douglasmotta.data.db.DbHelper
import br.com.douglasmotta.data.db.table.UserEntity
import br.com.douglasmotta.data.db.table.toModel
import br.com.douglasmotta.data.db.users
import br.com.douglasmotta.data.model.User
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.map

class UserDbLocalDataSourceImpl : UserLocalDataSource {

    private val database = DbHelper.database()

    override suspend fun getUsers(): List<User> {
        return database.users.map { it.toModel() }
    }

    override suspend fun getUserBy(id: Int): UserEntity? {
        return database.users.find { it.id eq id }
    }

    override suspend fun getUserByUsername(username: String): User? {
        return database.users.find { it.username eq  username }?.toModel()
    }

    override suspend fun insertUser(entity: UserEntity): Boolean {
        return database.users.add(entity) > 0
    }
}