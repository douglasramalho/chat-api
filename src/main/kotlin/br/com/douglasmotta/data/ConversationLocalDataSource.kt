package br.com.douglasmotta.data

import br.com.douglasmotta.data.model.Conversation
import br.com.douglasmotta.data.model.ConversationResult

interface ConversationLocalDataSource {

    suspend fun getAllConversations(): List<Conversation>

    suspend fun findConversationsBy(userId: String): List<ConversationResult>

    suspend fun findConversationBy(id: String): Conversation?

    suspend fun findConversationBy(firstId: String, secondId: String): ConversationResult?

    suspend fun insertConversation(conversation: Conversation): String?
}