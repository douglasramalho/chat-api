package br.com.douglasmotta.controller

import br.com.douglasmotta.data.ConversationLocalDataSource
import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.UserLocalDataSource
import br.com.douglasmotta.data.db.table.ConversationEntity
import br.com.douglasmotta.data.db.table.MessageEntity
import br.com.douglasmotta.data.db.table.toResponse
import br.com.douglasmotta.data.model.MemberAlreadyExistsException
import br.com.douglasmotta.data.request.MessageRequest
import br.com.douglasmotta.data.response.ConversationResponse
import br.com.douglasmotta.data.response.MessageResponse
import br.com.douglasmotta.data.model.ChatConnection
import br.com.douglasmotta.data.response.OnlineStatusResponse
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.util.*

class ChatController(
    private val messageLocalDataSource: MessageLocalDataSource,
    private val conversationLocalDataSource: ConversationLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource
) {

    private val connections = Collections.synchronizedSet<ChatConnection?>(LinkedHashSet())

    suspend fun onJoin(
        userId: Int,
        socket: DefaultWebSocketSession,
    ) {
        if (connections.firstOrNull { it.userId == userId } != null) {
            throw MemberAlreadyExistsException()
        }

        connections += ChatConnection(userId, socket)
        sendOnlineStatus()
    }

    suspend fun sendMessage(senderId: Int, messageRequest: MessageRequest) {
        val conversationEntity = conversationLocalDataSource.findConversationBy(senderId, messageRequest.receiverId)
        val newConversationEntity = if (conversationEntity == null) {
            val firstMember = userLocalDataSource.getUserBy(senderId) ?: return
            val secondMember = userLocalDataSource.getUserBy(messageRequest.receiverId) ?: return

            val newConversation = ConversationEntity {
                this.firstMember = firstMember
                this.secondMember = secondMember
                timestamp = Instant.now()
            }

            val conversationCreated = conversationLocalDataSource.insertConversation(newConversation)
            if (conversationCreated) {
                conversationLocalDataSource.findConversationBy(senderId, messageRequest.receiverId)
            } else null
        } else conversationEntity

        newConversationEntity?.let {
            val senderMember = if (it.firstMember.id == senderId) {
                it.firstMember
            } else it.secondMember

            val receiverMember = if (it.firstMember.id == messageRequest.receiverId) {
                it.firstMember
            } else it.secondMember

            val messageEntity = MessageEntity {
                sender = senderMember
                receiver = receiverMember
                text = messageRequest.text
                timestamp = Instant.now()
                isUnread = true
            }

            messageLocalDataSource.insertMessage(messageEntity)

            val messageResponse = messageEntity.toResponse()

            val messageResponseJsonText = Json.encodeToString<MessageResponse>(messageResponse)

            connections.forEach { connection ->
                if (connection.userId == senderId || connection.userId == messageRequest.receiverId) {
                    connection.session.send(Frame.Text("newMessage#$messageResponseJsonText"))
                }
            }

            sendConversations(messageRequest.receiverId)
        } ?: throw Exception("Conversation does not exist")
    }

    suspend fun sendConversations(userId: Int) {
        connections.firstOrNull { it.userId == userId }?.let { connection ->
            val conversations = conversationLocalDataSource.findConversationsBy(userId).map { conversationEntity ->
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
            val conversationsJsonText = Json.encodeToString<List<ConversationResponse>>(conversations)
            connection.session.send(Frame.Text("conversationsList#$conversationsJsonText"))
        }
    }

    suspend fun readMessage(messageId: Int) {
        messageLocalDataSource.markMessageAsRead(messageId)
    }

    suspend fun sendOnlineStatus() {
        val onlineUserIds = connections.map { it.userId }
        val onlineUserIdsJsonText = Json.encodeToString<OnlineStatusResponse>(OnlineStatusResponse(onlineUserIds))
        connections.forEach {
            it.session.send(Frame.Text("onlineUserIds#$onlineUserIdsJsonText"))
        }
    }

    suspend fun tryDisconnect(userId: Int) {
        connections.firstOrNull { it.userId == userId }?.let { connection ->
            connection.session.close()
            connections -= connection
        }

        sendOnlineStatus()
    }
}