package br.com.douglasmotta.controller

import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.model.Message

class MessageController(
    private val messageLocalDataSource: MessageLocalDataSource,
) {

    suspend fun createMessage(conversationId: String, senderId: String, text: String): Message {
        val message = Message(
            conversationId = conversationId,
            senderId = senderId,
            text = text,
            timestamp = System.currentTimeMillis(),
            isUnread = true
        )

        return messageLocalDataSource.insertMessage(message)
    }

    suspend fun getMessagesBy(conversationId: String): List<Message> {
        return messageLocalDataSource.findMessagesBy(conversationId)
    }

    suspend fun markMessageAsRead(messageId: String) {
        messageLocalDataSource.markMessageAsRead(messageId)
    }
}