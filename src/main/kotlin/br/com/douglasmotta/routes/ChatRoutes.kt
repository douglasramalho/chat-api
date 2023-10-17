package br.com.douglasmotta.routes

import br.com.douglasmotta.controller.ChatController
import br.com.douglasmotta.controller.ConversationController
import br.com.douglasmotta.data.model.MemberAlreadyExistsException
import br.com.douglasmotta.controller.MessageController
import br.com.douglasmotta.data.model.SocketAction
import br.com.douglasmotta.data.request.MessageRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
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

                        is SocketAction.GetConversations -> {
                            chatController.sendConversations(action.userId.toInt())
                        }

                        is SocketAction.MarkMessageAsRead -> {
                            chatController.readMessage(action.messageId.toInt())
                        }

                        is SocketAction.GetOnlineStatus -> {
                            chatController.sendOnlineStatus(action.userId.toInt(), action.receiverId.toInt())
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
        "getConversations" -> {
            SocketAction.GetConversations(body)
        }

        "newMessage" -> {
            val request = Json.decodeFromString<MessageRequest>(body)
            SocketAction.NewMessage(request)
        }

        "markMessageAsRead" -> {
            SocketAction.MarkMessageAsRead(body)
        }

        else -> null
    }
}

fun Route.conversation(conversationController: ConversationController) {
    get("/conversations/{userId}") {
        val userId = call.parameters["userId"] ?: throw IllegalArgumentException("UserId missing")
        val conversations = conversationController.getConversationsBy(userId.toInt())
        call.respond(conversations)
    }

    get("/conversations/find/{firstId}/{secondId}") {
        val firstId = call.parameters["firstId"] ?: throw IllegalArgumentException("FirstId missing")
        val secondId = call.parameters["secondId"] ?: throw IllegalArgumentException("SecondId missing")
        val conversation = conversationController.findConversationBy(firstId.toInt(), secondId.toInt())
        conversation?.let {
            call.respond(it)
        } ?: call.respond(HttpStatusCode(404, "Conversation not found"))
    }
}

fun Route.message(messageController: MessageController) {
    get("/messages/{senderId}/{receiverId}") {
        val senderId = call.parameters["senderId"] ?: throw IllegalArgumentException("senderId missing")
        val receiverId = call.parameters["receiverId"] ?: throw IllegalArgumentException("receiverId missing")

        try {
            val messages = messageController.getMessagesBy(senderId.toInt(), receiverId.toInt())
            call.respond(messages)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e)
        }
    }
}