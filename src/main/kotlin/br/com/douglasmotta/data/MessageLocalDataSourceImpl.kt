package br.com.douglasmotta.data

import br.com.douglasmotta.data.model.Message
import org.litote.kmongo.coroutine.CoroutineDatabase

class MessageLocalDataSourceImpl(
    private val db: CoroutineDatabase
) : MessageLocalDataSource {

    private val messagesCollection = db.getCollection<Message>()

    override suspend fun getAllMessages(): List<Message> {
        return messagesCollection.find()
            .descendingSort(Message::timestamp)
            .toList()
    }

    override suspend fun insertMessage(message: Message) {
        messagesCollection.insertOne(message)
    }
}