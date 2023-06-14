package br.com.douglasmotta.data

import br.com.douglasmotta.data.model.Conversation
import br.com.douglasmotta.data.model.ConversationResult
import br.com.douglasmotta.data.model.Message
import br.com.douglasmotta.data.model.User
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate

class ConversationLocalDataSourceImpl(
    db: CoroutineDatabase
) : ConversationLocalDataSource {

    private val conversationsCollection = db.getCollection<Conversation>()

    override suspend fun getAllConversations(): List<Conversation> {
        return conversationsCollection
            .find()
            .descendingSort(Conversation::timestamp)
            .toList()
    }

    override suspend fun findConversationsBy(userId: String): List<ConversationResult> {
        return conversationsCollection.aggregate<ConversationResult>(
            match(Conversation::members contains userId),
            lookup("user", "members", "_id", "users"),
            lookup("message", "_id", "conversationId", "messages"),
            sort(descending(Message::timestamp))
            /*lookup(
                from = "message",
                let = listOf(Conversation::id.variableDefinition("id"), Message::conversationId.variableDefinition()),
                resultProperty = ConversationResult::messages,
                pipeline = arrayOf(
                    match(Message::conversationId eq Conversation::id.variable),
                    sort(descending(Message::timestamp)),
                    limit(5)
                )
            ),*/
            /*lookup(
                from = "message",
                let = listOf(Message::conversationId.variableDefinition()),
                resultProperty = ConversationResult::unreadMessages,
                pipeline = arrayOf(
                    match(Conversation::id eq Message::conversationId.variable),
                    match(Message::isUnread eq true),
                )
            ),*/

            ).toList()
    }

    override suspend fun findConversationBy(id: String): Conversation? {
        return conversationsCollection
            .findOne(Conversation::id eq id)
    }

    override suspend fun findConversationBy(firstId: String, secondId: String): ConversationResult? {
        return conversationsCollection.aggregate<ConversationResult>(
            match(
                and(
                    Conversation::members / User::id eq firstId,
                    Conversation::members / User::id eq secondId
                )
            ),
            lookup("user", "members", "_id", "users"), // Faz o join com a coleção de usuários
            lookup("message", "_id", "conversationId", "messages")
        ).first()

    }

    override suspend fun insertConversation(conversation: Conversation): String? {
        return conversationsCollection.insertOne(conversation).insertedId?.toString()
    }
}