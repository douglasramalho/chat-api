package br.com.douglasmotta.data.model

import br.com.douglasmotta.data.response.ConversationResponse
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class ConversationResult(
    @BsonId
    val id: String = ObjectId().toString(),
    val users: ArrayList<User>,
    val messages: ArrayList<Message>?,
    val timestamp: Long
)

fun ConversationResult.toResponse(userId: String) = ConversationResponse(
    id = this.id,
    members = this.users.map { it.toResponse() },
    unreadCount = messages?.count { it.senderId != userId && it.isUnread } ?: 0,
    lastMessage = if (!messages.isNullOrEmpty()) messages.last().text else null,
    timestamp = if (!messages.isNullOrEmpty()) messages.last().timestamp else this.timestamp,
)