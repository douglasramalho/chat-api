package br.com.douglasmotta.data.db.table

import br.com.douglasmotta.data.model.Message
import br.com.douglasmotta.data.response.MessageResponse
import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.Instant

interface MessageEntity : Entity<MessageEntity> {
    companion object : Entity.Factory<MessageEntity>()
    val id: Int
    var sender: UserEntity
    var receiver: UserEntity
    var text: String
    var timestamp: Instant
    var isUnread: Boolean
}

object Messages: Table<MessageEntity>("messages") {

    val id = int("id").primaryKey().bindTo { it.id }
    val senderId = int("sender_id").references(Users) { it.sender }
    val receiverId = int("receiver_id").references(Users) { it.receiver }
    val text = varchar("text").bindTo { it.text }
    val timestamp = timestamp("timestamp").bindTo { it.timestamp }
    val isUnread = boolean("is_unread").bindTo { it.isUnread }
}

fun MessageEntity.toModel() = Message(
    id = this.id,
    sender = this.sender.toModel(),
    receiver = this.receiver.toModel(),
    text = this.text,
    timestamp = this.timestamp,
    isUnread = this.isUnread,
)

fun MessageEntity.toResponse() = MessageResponse(
    id = this.id,
    senderId = this.sender.id,
    receiverId = this.receiver.id,
    text = this.text,
    timestamp = this.timestamp.toEpochMilli(),
    isUnread = this.isUnread,
)