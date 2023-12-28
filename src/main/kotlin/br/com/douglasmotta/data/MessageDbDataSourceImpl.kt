package br.com.douglasmotta.data

import br.com.douglasmotta.data.db.DbHelper
import br.com.douglasmotta.data.db.messages
import br.com.douglasmotta.data.db.table.Conversations
import br.com.douglasmotta.data.db.table.MessageEntity
import br.com.douglasmotta.data.db.table.Messages
import br.com.douglasmotta.data.db.table.toModel
import br.com.douglasmotta.data.model.Message
import org.ktorm.dsl.*
import org.ktorm.dsl.map
import org.ktorm.entity.*

class MessageDbDataSourceImpl : MessageLocalDataSource {

    private val database = DbHelper.database()

    override suspend fun findMessagesBy(
        senderId: Int,
        receiverId: Int,
        offset: Int,
        limit: Int,
    ): List<MessageEntity> {

        val result = database
            .from(Messages)
            .joinReferencesAndSelect()
            .where {
                (Messages.senderId eq senderId and (Messages.receiverId eq receiverId)) or(Messages.senderId eq receiverId and (Messages.receiverId eq senderId))
            }
            .limit(offset = offset, limit = limit)
            .orderBy(Messages.timestamp.desc())
            .map { row ->
                Messages.createEntity(row)
            }

        return result
    }

    override suspend fun getTotalMessagesCount(): Int {
        return database.from(Messages).select().totalRecordsInAllPages
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

    override suspend fun markMessagesAsRead(senderId: Int, receiverId: Int) {
        val entities = database.messages.filter {
            ((it.senderId eq senderId and (it.receiverId eq receiverId)) or(it.senderId eq receiverId and (it.receiverId eq senderId))) and (it.isUnread eq true) }
        entities.forEach {
            it.isUnread = false
            it.flushChanges()
        }
    }

    override suspend fun insertMessage(entity: MessageEntity): Int {
        return database.messages.add(entity)
    }
}