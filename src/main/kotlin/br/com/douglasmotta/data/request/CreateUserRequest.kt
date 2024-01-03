package br.com.douglasmotta.data.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val profilePictureId: Int? = null,
)
