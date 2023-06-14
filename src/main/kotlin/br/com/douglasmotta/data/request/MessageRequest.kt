package br.com.douglasmotta.data.request

import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    val conversationId: String,
    val text: String,
)