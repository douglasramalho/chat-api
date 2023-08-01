package br.com.douglasmotta.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ConversationResponse(
    val id: Int,
    val members: List<UserResponse>,
    val unreadCount: Int,
    val lastMessage: String? = null,
    val timestamp: Long
)