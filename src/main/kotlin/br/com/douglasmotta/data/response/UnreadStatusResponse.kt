package br.com.douglasmotta.data.response

import kotlinx.serialization.Serializable

@Serializable
data class UnreadStatusResponse(
    val hasConversationsUnread: Boolean,
    val unreadMessagesCount: Int,
)