package br.com.douglasmotta.data

import br.com.douglasmotta.data.model.Message

interface MessageLocalDataSource {

    suspend fun findMessagesBy(conversationId: String): List<Message>

    suspend fun findLastMessageBy(conversationId: String): Message?

    suspend fun totalUnread(conversationId: String, userId: String): Int

    suspend fun markMessageAsRead(messageId: String)

    suspend fun insertMessage(message: Message): Message
}