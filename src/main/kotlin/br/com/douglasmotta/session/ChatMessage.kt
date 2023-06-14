package br.com.douglasmotta.session

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val messageId: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: Long
)
