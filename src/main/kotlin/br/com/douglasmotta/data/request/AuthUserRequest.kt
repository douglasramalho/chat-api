package br.com.douglasmotta.data.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthUserRequest(
    val username: String,
    val password: String,
)
