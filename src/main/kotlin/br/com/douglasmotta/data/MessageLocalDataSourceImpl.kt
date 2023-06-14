package br.com.douglasmotta.data

import br.com.douglasmotta.data.model.Message
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class MessageLocalDataSourceImpl(
    db: CoroutineDatabase
) : MessageLocalDataSource {

    private val messagesCollection = db.getCollection<Message>()

    override suspend fun findMessagesBy(conversationId: String): List<Message> {
        return messagesCollection
            .find(Message::conversationId eq conversationId)
            .descendingSort(Message::timestamp)
            .limit(10)
            .toList()
    }

    override suspend fun findLastMessageBy(conversationId: String): Message? {
        return messagesCollection
            .find(Message::conversationId eq conversationId)
            .descendingSort(Message::timestamp)
            .first()
    }

    override suspend fun totalUnread(conversationId: String, userId: String): Int {
        return messagesCollection
            .countDocuments(
                and(
                    Message::conversationId eq conversationId,
                    Message::senderId ne userId,
                    Message::isUnread eq true
                )
            ).toInt()
    }

    override suspend fun markMessageAsRead(messageId: String) {
        messagesCollection.updateOneById(
            messageId,
            setValue(Message::isUnread, false)
        )
    }

    override suspend fun insertMessage(message: Message): Message {
        messagesCollection.insertOne(message)
        return message
    }
}