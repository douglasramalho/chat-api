package br.com.douglasmotta.data.model

import br.com.douglasmotta.data.request.MessageRequest

sealed class SocketAction {
    data class NewMessage(val request: MessageRequest) : SocketAction()
    data class GetConversations(val userId: String) : SocketAction()
    data class MarkMessageAsRead(val messageId: String) : SocketAction()
}