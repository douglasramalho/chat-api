package br.com.douglasmotta.controller

import br.com.douglasmotta.data.ConversationLocalDataSource
import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.db.table.toResponse
import br.com.douglasmotta.data.response.ConversationResponse
import br.com.douglasmotta.data.response.ConversationsPaginatedResponse

class ConversationController(
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val messageLocalDataSource: MessageLocalDataSource,
) {

    suspend fun getConversationsBy(
        userId: Int,
        offset: Int,
        limit: Int
    ): ConversationsPaginatedResponse {
        val conversations = conversationLocalDataSource.findConversationsBy(
            userId = userId,
            offset = offset,
            limit = limit,
        ).map { conversationEntity ->
            val lastMessage = messageLocalDataSource.findLastMessageBy(
                conversationEntity.firstMember.id,
                conversationEntity.secondMember.id,
            )

            val users = listOf(conversationEntity.firstMember.id, conversationEntity.secondMember.id)

            val otherId = users.first { it != userId }
            val unreadCount = messageLocalDataSource.getUnreadCount(
                otherId,
                userId
            )

            conversationEntity.toResponse(lastMessage?.text, unreadCount)
        }

        val totalConversationsCount = conversationLocalDataSource.getTotalConversationsCount()

        return ConversationsPaginatedResponse(
            conversations = conversations,
            total = totalConversationsCount,
            hasMore = offset + limit < totalConversationsCount
        )
    }

    suspend fun findConversationBy(firstId: Int, secondId: Int): ConversationResponse? {
        return conversationLocalDataSource.findConversationBy(firstId, secondId)?.let {
            val lastMessage = messageLocalDataSource.findLastMessageBy(firstId, secondId)
            val unreadCount = messageLocalDataSource.getUnreadCount(firstId, secondId)

            return it.toResponse(lastMessage = lastMessage?.text, unreadCount = unreadCount)
        }
    }
}