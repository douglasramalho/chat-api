package br.com.douglasmotta.data

import br.com.douglasmotta.data.db.DbHelper
import br.com.douglasmotta.data.db.conversations
import br.com.douglasmotta.data.db.table.ConversationEntity
import br.com.douglasmotta.data.db.table.Conversations
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find

class ConversationDbDataSourceImpl : ConversationLocalDataSource {

    private val database = DbHelper.database()

    override suspend fun findConversationsBy(
        userId: Int,
        offset: Int,
        limit: Int,
    ): List<ConversationEntity> {
        val result = database
            .from(Conversations)
            .joinReferencesAndSelect()
            .where {
                Conversations.firstMemberId eq userId or (Conversations.secondMemberId eq userId)
            }
            .limit(offset = offset, limit = limit)
            .orderBy(Conversations.timestamp.asc())
            .map { row ->
                Conversations.createEntity(row)
            }

        return result
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

    override suspend fun getTotalConversationsCount(): Int {
        return database.from(Conversations).select().totalRecordsInAllPages
    }
}