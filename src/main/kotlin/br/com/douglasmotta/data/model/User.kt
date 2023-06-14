package br.com.douglasmotta.data.model

import br.com.douglasmotta.data.response.UserResponse
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: String = ObjectId().toString(),
    val username: String,
    val password: String,
    val salt: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?
)

fun User.toResponse() = UserResponse(
    id = this.id,
    username = this.username,
    firstName = this.firstName,
    lastName = this.lastName,
    profilePictureUrl = this.profilePictureUrl,
)
