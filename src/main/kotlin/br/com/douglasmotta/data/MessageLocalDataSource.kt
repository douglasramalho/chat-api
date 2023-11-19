package br.com.douglasmotta.data

import br.com.douglasmotta.data.db.table.MessageEntity
import br.com.douglasmotta.data.model.Message

interface MessageLocalDataSource {

    // suspend fun findMessagesBy(conversationId: String): List<Message>

    suspend fun findMessagesBy(
        senderId: Int,
        receiverId: Int,
        offset: Int,
        limit: Int,
    ): List<MessageEntity>

    suspend fun getTotalMessagesCount(): Int

    suspend fun findLastMessageBy(senderId: Int, receiverId: Int): Message?

    suspend fun getUnreadCount(senderId: Int, receiverId: Int): Int

    suspend fun markMessageAsRead(messageId: Int)

    suspend fun insertMessage(entity: MessageEntity): Int
}