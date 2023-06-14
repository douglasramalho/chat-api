package br.com.douglasmotta.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Message(
    @BsonId
    val id: String = ObjectId().toString(),
    val conversationId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isUnread: Boolean
)