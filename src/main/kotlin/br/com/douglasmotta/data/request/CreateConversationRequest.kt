package br.com.douglasmotta.data.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateConversationRequest(
    val senderId: String,
    val receiverId: String,
    val text: String,
)