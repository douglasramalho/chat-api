package br.com.douglasmotta.session

import io.ktor.websocket.DefaultWebSocketSession

data class ConversationConnection(
    val userId: String, val session: DefaultWebSocketSession
)