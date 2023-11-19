package br.com.douglasmotta.controller

import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.db.table.toResponse
import br.com.douglasmotta.data.response.MessageResponse
import br.com.douglasmotta.data.response.MessagesPaginatedResponse

class MessageController(
    private val messageLocalDataSource: MessageLocalDataSource,
) {

    suspend fun getMessagesBy(
        senderId: Int,
        receiverId: Int,
        offset: Int,
        limit: Int,
    ): MessagesPaginatedResponse {
        val messages = messageLocalDataSource.findMessagesBy(
            senderId,
            receiverId,
            offset,
            limit,
        ).map {
            it.toResponse()
        }

        val totalMessagesCount = messageLocalDataSource.getTotalMessagesCount()

        return MessagesPaginatedResponse(
            messages = messages,
            total = totalMessagesCount,
            hasMore = offset + limit < totalMessagesCount
        )
    }
}