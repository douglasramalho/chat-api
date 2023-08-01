package br.com.douglasmotta.data.response

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val text: String,
    val timestamp: Long,
    val isUnread: Boolean,
)