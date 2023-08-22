package br.com.douglasmotta.data

import br.com.douglasmotta.data.db.DbHelper
import br.com.douglasmotta.data.db.messages
import br.com.douglasmotta.data.db.table.MessageEntity
import br.com.douglasmotta.data.db.table.toModel
import br.com.douglasmotta.data.model.Message
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.or
import org.ktorm.entity.*

class MessageDbDataSourceImpl : MessageLocalDataSource {

    private val database = DbHelper.database()

    override suspend fun findMessagesBy(senderId: Int, receiverId: Int): List<MessageEntity> {
        return database.messages
            .filter {
                (it.senderId eq senderId and (it.receiverId eq receiverId)) or (it.senderId eq receiverId and (it.receiverId eq senderId))
            }.sortedByDescending { it.timestamp }
            .take(10)
            .map { it }
    }

    override suspend fun findLastMessageBy(senderId: Int, receiverId: Int): Message? {
        val entity = database.messages.findLast {
            (it.senderId eq senderId and (it.receiverId eq receiverId)) or(it.senderId eq receiverId and (it.receiverId eq senderId))
        }
        return entity?.toModel()
    }

    override suspend fun getUnreadCount(senderId: Int, receiverId: Int): Int {
        return database.messages.count { it.senderId eq senderId and (it.receiverId eq receiverId) and (it.isUnread eq true) }

    }

    override suspend fun markMessageAsRead(messageId: Int) {
        val entity = database.messages.find { it.id eq messageId } ?: return
        entity.isUnread = false
        entity.flushChanges()
    }

    override suspend fun insertMessage(entity: MessageEntity): Int {
        return database.messages.add(entity)
    }
}