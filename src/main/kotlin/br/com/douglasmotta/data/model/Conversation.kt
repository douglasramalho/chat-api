package br.com.douglasmotta.data.model

import java.time.Instant

data class Conversation(
    val id: Int = 0,
    val firstMember: User,
    val secondMember: User,
    val timestamp: Instant,
    val isUnread: Boolean,
    val lastMessage: String? = null
)