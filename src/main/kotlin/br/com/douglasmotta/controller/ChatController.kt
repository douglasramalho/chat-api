package br.com.douglasmotta.controller

import br.com.douglasmotta.data.ConversationLocalDataSource
import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.model.Conversation
import br.com.douglasmotta.data.model.Message
import br.com.douglasmotta.data.model.toResponse
import br.com.douglasmotta.data.request.CurrentScreenRequest
import br.com.douglasmotta.data.request.MessageRequest
import br.com.douglasmotta.data.response.ConversationResponse
import br.com.douglasmotta.data.response.MessageResponse
import br.com.douglasmotta.session.ChatConnection
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class ChatController(
    private val messageLocalDataSource: MessageLocalDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
) {

    private val connections = Collections.synchronizedSet<ChatConnection?>(LinkedHashSet())

    fun onJoin(
        userId: String,
        socket: DefaultWebSocketSession,
    ) {
        if (connections.firstOrNull { it.userId == userId } != null) {
            throw MemberAlreadyExistsException()
        }

        connections += ChatConnection(userId, socket)
    }

    suspend fun sendMessage(senderId: String, messageRequest: MessageRequest) {
        val conversation = conversationLocalDataSource.findConversationBy(messageRequest.conversationId)
        conversation?.let {
            val receiverId = it.members.first { userId ->
                userId != senderId
            }

            val message = Message(
                conversationId = it.id,
                senderId = senderId,
                text = messageRequest.text,
                timestamp = System.currentTimeMillis(),
                isUnread = true
            )
            messageLocalDataSource.insertMessage(message)

            val messageResponse = MessageResponse(
                id = message.id,
                conversationId = message.conversationId,
                senderId = message.senderId,
                text = message.text,
                timestamp = message.timestamp,
                isUnread = message.isUnread
            )

            val messageResponseJsonText = Json.encodeToString<MessageResponse>(messageResponse)

            connections.forEach { connection ->
                if (connection.userId == senderId || connection.userId == receiverId) {
                    connection.session.send(Frame.Text(messageResponseJsonText))
                }
            }

            sendConversations(receiverId)
        } ?: throw Exception("Conversation does not exist")
    }

    suspend fun sendConversations(userId: String) {
        connections.firstOrNull { it.userId == userId }?.let { connection ->
            val conversations = conversationLocalDataSource.findConversationsBy(userId).map {
                it.toResponse(userId)
            }
            val conversationsJsonText = Json.encodeToString<List<ConversationResponse>>(conversations)
            connection.session.send(Frame.Text("conversationsList#$conversationsJsonText"))
        }
    }

    suspend fun readMessage(messageId: String) {
        messageLocalDataSource.markMessageAsRead(messageId)
    }

    suspend fun tryDisconnect(userId: String) {
        connections.firstOrNull { it.userId == userId }?.let { connection ->
            connection.session.close()
            connections -= connection
        }
    }
}