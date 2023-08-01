package br.com.douglasmotta.data

import br.com.douglasmotta.data.db.table.ConversationEntity
import br.com.douglasmotta.data.model.Conversation

interface ConversationLocalDataSource {

    suspend fun findConversationsBy(userId: Int): List<ConversationEntity>

    suspend fun findConversationBy(firstId: Int, secondId: Int): ConversationEntity?

    suspend fun insertConversation(entity: ConversationEntity): Boolean
}