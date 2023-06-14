package br.com.douglasmotta.controller

import br.com.douglasmotta.data.ConversationLocalDataSource
import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.UserDataSource
import br.com.douglasmotta.data.model.Conversation
import br.com.douglasmotta.data.model.toResponse
import br.com.douglasmotta.data.response.ConversationResponse
import br.com.douglasmotta.session.ChatConnection
import br.com.douglasmotta.session.ConversationConnection
import io.ktor.websocket.*
import java.util.*

class ConversationController(
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val userDataSource: UserDataSource,
) {

    suspend fun createConversation(
        senderId: String,
        receiverId: String,
    ): String? {

        val sender = userDataSource.getUserBy(senderId)
        val receiver = userDataSource.getUserBy(receiverId)

        return if (sender != null && receiver != null) {
            val conversation = Conversation(
                members = arrayListOf(
                    sender.id,
                    receiver.id
                ),
                timestamp = System.currentTimeMillis(),
            )

            conversationLocalDataSource.insertConversation(conversation)
        } else null
    }

    suspend fun getConversationsBy(userId: String): List<ConversationResponse> {
        return conversationLocalDataSource.findConversationsBy(userId).map { conversation ->
            conversation.toResponse(userId)
        }
    }

    suspend fun findConversationsBy(firstId: String, secondId: String): ConversationResponse? {
        return conversationLocalDataSource.findConversationBy(firstId, secondId)?.toResponse(firstId)
    }
}