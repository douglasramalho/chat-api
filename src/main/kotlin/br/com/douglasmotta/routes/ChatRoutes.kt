package br.com.douglasmotta.routes

import br.com.douglasmotta.controller.ChatController
import br.com.douglasmotta.controller.ConversationController
import br.com.douglasmotta.controller.MemberAlreadyExistsException
import br.com.douglasmotta.controller.MessageController
import br.com.douglasmotta.data.request.CreateConversationRequest
import br.com.douglasmotta.data.request.CurrentScreenRequest
import br.com.douglasmotta.data.request.MessageRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun Route.chatSocket(chatController: ChatController, conversationController: ConversationController) {
    webSocket("/chat/{userId}") {
        val userId = call.parameters["userId"] ?: throw IllegalArgumentException("UserId missing")
        try {
            chatController.onJoin(userId, this)
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    when (val action = extractAction(frame.readText())) {
                        is Action.NewMessage ->
                            chatController.sendMessage(userId, action.request)

                        is Action.GetConversations -> {
                            chatController.sendConversations(action.userId)
                        }

                        is Action.MarkMessageAsRead -> {
                            chatController.readMessage(action.messageId)
                        }

                        else -> {
                        }
                    }
                }
            }
        } catch (e: MemberAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e)
        } finally {
            chatController.tryDisconnect(userId)
        }
    }
}

private fun extractAction(message: String): Action? {
    val type = message.substringBefore("#")
    val body = message.substringAfter("#")
    return when (type) {
        "getConversations" -> {
            Action.GetConversations(body)
        }

        "newMessage" -> {
            val request = Json.decodeFromString<MessageRequest>(body)
            Action.NewMessage(request)
        }

        "markMessageAsRead" -> {
            Action.MarkMessageAsRead(body)
        }

        else -> null
    }
}


sealed class Action {
    data class NewMessage(val request: MessageRequest) : Action()
    data class GetConversations(val userId: String) : Action()
    data class MarkMessageAsRead(val messageId: String) : Action()
}

fun Route.conversation(conversationController: ConversationController, messageController: MessageController) {
    post("/conversations") {
        try {
            val dto = call.receive<CreateConversationRequest>()
            val conversationId = conversationController.createConversation(dto.senderId, dto.receiverId)

            if (conversationId == null) {
                call.respond(HttpStatusCode.InternalServerError, "Error when creating the new conversation")
                return@post
            }

            val result = messageController.createMessage(
                conversationId = conversationId,
                senderId = dto.senderId,
                text = dto.text
            )

            call.respond(result)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e)
        }
    }

    get("/conversations/{userId}") {
        val userId = call.parameters["userId"] ?: throw IllegalArgumentException("UserId missing")
        val conversations = conversationController.getConversationsBy(userId)
        call.respond(conversations)
    }

    get("/conversations/find/{firstId}/{secondId}") {
        val firstId = call.parameters["firstId"] ?: throw IllegalArgumentException("FirstId missing")
        val secondId = call.parameters["secondId"] ?: throw IllegalArgumentException("SecondId missing")
        val conversation = conversationController.findConversationsBy(firstId, secondId)
        conversation?.let {
            call.respond(it)
        } ?: call.respond(HttpStatusCode(404, "Conversation not found"))
    }
}

fun Route.message(messageController: MessageController, conversationController: ConversationController) {
    post("/messages/{userId}") {
        /*val receiverId = call.parameters["userId"] ?: throw IllegalArgumentException("userId missing")
        try {
            val dto = call.receive<MessageRequest>()

            val conversationId = conversationController.createConversation(
                senderId = dto.,
                receiverId = receiverId
            )

            if (conversationId == null) {
                call.respond(HttpStatusCode.InternalServerError, "Error when creating the new conversation")
                return@post
            }

            val result = messageController.createMessage(
                conversationId = conversationId,
                senderId = dto.senderId,
                text = dto.text,
            )

            call.respond(result)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e)
        }*/
    }

    get("/messages/{conversationId}") {
        val conversationId =
            call.parameters["conversationId"] ?: throw IllegalArgumentException("conversationId missing")

        try {
            val messages = messageController.getMessagesBy(conversationId)
            call.respond(messages)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e)
        }
    }
}