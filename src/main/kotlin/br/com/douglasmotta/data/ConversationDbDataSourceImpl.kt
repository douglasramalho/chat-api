package br.com.douglasmotta.data

import br.com.douglasmotta.data.db.DbHelper
import br.com.douglasmotta.data.db.conversations
import br.com.douglasmotta.data.db.table.ConversationEntity
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.or
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.map

class ConversationDbDataSourceImpl : ConversationLocalDataSource {

    private val database = DbHelper.database()

    override suspend fun findConversationsBy(userId: Int): List<ConversationEntity> {
        return database.conversations.filter {
            it.firstMemberId eq userId or(it.secondMemberId eq userId)
        }.map { it }
    }

    override suspend fun findConversationBy(firstId: Int, secondId: Int): ConversationEntity? {
        return database.conversations.find {
            (it.firstMemberId eq firstId and (it.secondMemberId eq secondId)) or
                    (it.firstMemberId eq secondId and (it.secondMemberId eq firstId))
        }
    }

    override suspend fun insertConversation(entity: ConversationEntity): Boolean {
        return database.conversations.add(entity) > 0
    }
}