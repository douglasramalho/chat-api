package br.com.douglasmotta.data.db.table

import br.com.douglasmotta.data.model.User
import br.com.douglasmotta.data.response.UserResponse
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface UserEntity : Entity<UserEntity> {
    companion object : Entity.Factory<UserEntity>()
    val id: Int
    var username: String
    var password: String
    var salt: String
    var firstName: String
    var lastName: String
    var profilePictureUrl: String?
}

object Users: Table<UserEntity>("users") {
    val id = int("id").primaryKey().bindTo { it.id }
    val username = varchar("username").bindTo { it.username }
    val password = varchar("password").bindTo { it.password }
    val salt = varchar("salt").bindTo { it.salt }
    val firstName = varchar("first_name").bindTo { it.firstName }
    val lastName = varchar("last_name").bindTo { it.lastName }
    val profilePictureUrl = varchar("profile_picture_url").bindTo { it.profilePictureUrl }
}

fun UserEntity.toModel() = User(
    id = this.id,
    username = this.username,
    password = this.password,
    salt = this.salt,
    firstName = this.firstName,
    lastName = this.lastName,
    profilePictureUrl = this.profilePictureUrl
)

fun UserEntity.toResponse() = UserResponse(
    id = this.id,
    username = this.username,
    firstName = this.firstName,
    lastName = this.lastName,
    profilePictureUrl = this.profilePictureUrl,
)