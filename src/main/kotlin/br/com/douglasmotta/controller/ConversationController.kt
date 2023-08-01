package br.com.douglasmotta.controller

import br.com.douglasmotta.data.ConversationLocalDataSource
import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.db.table.toResponse
import br.com.douglasmotta.data.response.ConversationResponse

class ConversationController(
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val messageLocalDataSource: MessageLocalDataSource,
) {

    suspend fun getConversationsBy(userId: Int): List<ConversationResponse> {
        return conversationLocalDataSource.findConversationsBy(userId).map {

            val firstId = it.firstMember.id
            val secondId = it.secondMember.id

            val lastMessage = messageLocalDataSource.findLastMessageBy(firstId, secondId)
            val unreadCount = messageLocalDataSource.getUnreadCount(firstId, secondId)

            it.toResponse(lastMessage = lastMessage?.text, unreadCount = unreadCount)
        }
    }

    suspend fun findConversationBy(firstId: Int, secondId: Int): ConversationResponse? {
        return conversationLocalDataSource.findConversationBy(firstId, secondId)?.let {
            val lastMessage = messageLocalDataSource.findLastMessageBy(firstId, secondId)
            val unreadCount = messageLocalDataSource.getUnreadCount(firstId, secondId)

            return it.toResponse(lastMessage = lastMessage?.text, unreadCount = unreadCount)
        }
    }
}