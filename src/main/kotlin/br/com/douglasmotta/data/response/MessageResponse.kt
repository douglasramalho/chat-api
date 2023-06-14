package br.com.douglasmotta.data.response

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isUnread: Boolean,
)