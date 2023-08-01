package br.com.douglasmotta.data.request

import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    val receiverId: Int,
    val text: String,
)