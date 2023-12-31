package br.com.douglasmotta.controller

import br.com.douglasmotta.data.ConversationLocalDataSource
import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.UserLocalDataSource
import br.com.douglasmotta.data.db.table.ConversationEntity
import br.com.douglasmotta.data.db.table.MessageEntity
import br.com.douglasmotta.data.db.table.toResponse
import br.com.douglasmotta.data.model.MemberAlreadyExistsException
import br.com.douglasmotta.data.request.MessageRequest
import br.com.douglasmotta.data.response.MessageResponse
import br.com.douglasmotta.data.model.ChatConnection
import br.com.douglasmotta.data.response.OnlineStatusResponse
import br.com.douglasmotta.data.response.UnreadStatusResponse
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
                createdAt = Instant.now()
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

            val messageTimestamp = Instant.now()
            val messageEntity = MessageEntity {
                sender = senderMember
                receiver = receiverMember
                text = messageRequest.text
                timestamp = messageTimestamp
                isUnread = true
            }

            messageLocalDataSource.insertMessage(messageEntity)

            it.updatedAt = messageTimestamp
            conversationLocalDataSource.updateConversation(it)

            val messageResponse = messageEntity.toResponse()

            val messageResponseJsonText = Json.encodeToString<MessageResponse>(messageResponse)

            connections.forEach { connection ->
                if (connection.userId == senderId || connection.userId == messageRequest.receiverId) {
                    connection.session.send(Frame.Text("newMessage#$messageResponseJsonText"))
                }
            }

            sendUnreadStatus(messageRequest.receiverId, senderId)
        } ?: throw Exception("Conversation does not exist")
    }

    private suspend fun sendUnreadStatus(userId: Int, otherId: Int) {
        connections.firstOrNull { it.userId == userId }?.let { connection ->
            val unreadMessagesCount = messageLocalDataSource.getUnreadCount(
                otherId,
                userId
            )

            val unreadStatusJsonText = Json.encodeToString<UnreadStatusResponse>(
                UnreadStatusResponse(
                    hasConversationsUnread = unreadMessagesCount > 0,
                    unreadMessagesCount = unreadMessagesCount,
                )
            )
            connection.session.send(Frame.Text("unreadStatus#$unreadStatusJsonText"))
        }
    }

    suspend fun readMessage(messageId: Int) {
        messageLocalDataSource.markMessageAsRead(messageId)
    }

    suspend fun sendOnlineStatus() {
        val onlineUserIds = connections.map { it.userId }
        val onlineUserIdsJsonText = Json.encodeToString<OnlineStatusResponse>(OnlineStatusResponse(onlineUserIds))
        connections.forEach {
            it.session.send(Frame.Text("activeUserIds#$onlineUserIdsJsonText"))
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