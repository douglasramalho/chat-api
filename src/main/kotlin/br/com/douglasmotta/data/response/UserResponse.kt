package br.com.douglasmotta.data.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?
)
