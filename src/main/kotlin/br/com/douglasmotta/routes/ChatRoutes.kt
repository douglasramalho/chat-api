package br.com.douglasmotta.routes

import br.com.douglasmotta.controller.ChatController
import br.com.douglasmotta.data.model.MemberAlreadyExistsException
import br.com.douglasmotta.data.model.SocketAction
import br.com.douglasmotta.data.request.MessageRequest
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

fun Route.chatSocket(chatController: ChatController) {
    webSocket("/chat/{userId}") {
        val userId = call.parameters["userId"] ?: throw IllegalArgumentException("UserId missing")
        try {
            chatController.onJoin(userId.toInt(), this)
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    when (val action = extractAction(frame.readText())) {
                        is SocketAction.NewMessage ->
                            chatController.sendMessage(userId.toInt(), action.request)

                        is SocketAction.MarkMessageAsRead -> {
                            chatController.readMessage(action.messageId.toInt())
                        }

                        is SocketAction.GetOnlineStatus -> {
                            chatController.sendOnlineStatus()
                        }

                        else -> {
                        }
                    }
                }
            }
        } catch (e: MemberAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "")
        } finally {
            chatController.tryDisconnect(userId.toInt())
        }
    }
}

private fun extractAction(message: String): SocketAction? {
    val type = message.substringBefore("#")
    val body = message.substringAfter("#")
    return when (type) {
        "newMessage" -> {
            val request = Json.decodeFromString<MessageRequest>(body)
            SocketAction.NewMessage(request)
        }

        "markMessageAsRead" -> {
            SocketAction.MarkMessageAsRead(body)
        }

        "getActiveStatus" -> {
            SocketAction.GetOnlineStatus
        }

        else -> null
    }
}

