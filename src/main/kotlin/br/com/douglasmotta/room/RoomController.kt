package br.com.douglasmotta.room

import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageLocalDataSource: MessageLocalDataSource,
) {

    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        username: String,
        sessionId: String,
        socket: WebSocketSession,
    ) {
        if (members.contains(username)) {
            throw MemberAlreadyExistsException()
        }

        members[username] = Member(username, sessionId, socket)
    }

    suspend fun sendMessage(senderUsername: String, message: String) {
        val messageEntity = Message(
            text = message,
            username = senderUsername,
            timestamp = System.currentTimeMillis()
        )

        messageLocalDataSource.insertMessage(messageEntity)

        members.values.forEach { member ->
            val parsedMessage = Json.encodeToString(messageEntity)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return messageLocalDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.contains(username)) {
            members.remove(username)
        }
    }
}