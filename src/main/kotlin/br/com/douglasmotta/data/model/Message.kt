package br.com.douglasmotta.data.model

import java.time.Instant

data class Message(
    val id: Int = 0,
    val sender: User,
    val receiver: User,
    val text: String,
    val timestamp: Instant,
    val isUnread: Boolean
)