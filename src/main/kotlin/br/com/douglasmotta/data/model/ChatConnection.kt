package br.com.douglasmotta.data.model

import io.ktor.websocket.DefaultWebSocketSession

data class ChatConnection(
    val userId: Int,
    val session: DefaultWebSocketSession
)