package br.com.douglasmotta.data.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
)
