package br.com.douglasmotta.controller

import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.db.table.toResponse
import br.com.douglasmotta.data.response.MessageResponse

class MessageController(
    private val messageLocalDataSource: MessageLocalDataSource,
) {

    suspend fun getMessagesBy(senderId: Int, receiverId: Int): List<MessageResponse> {
        return messageLocalDataSource.findMessagesBy(senderId, receiverId).map {
            it.toResponse()
        }
    }
}