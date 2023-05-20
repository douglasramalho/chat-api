package br.com.douglasmotta.data

import br.com.douglasmotta.data.model.Message

interface MessageLocalDataSource {

    suspend fun getAllMessages(): List<Message>

    suspend fun insertMessage(message: Message)
}