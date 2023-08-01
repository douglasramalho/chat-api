package br.com.douglasmotta.data.model

import br.com.douglasmotta.data.response.UserResponse

data class User(
    val id: Int = 0,
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
