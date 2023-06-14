package br.com.douglasmotta.session

import io.ktor.websocket.DefaultWebSocketSession

data class ChatConnection(
    val userId: String,
    val session: DefaultWebSocketSession,
    var currentScreen: String = "",
    var unreadMessagesCount: Int = 0
)