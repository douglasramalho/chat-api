package br.com.douglasmotta.routes

import br.com.douglasmotta.controller.ConversationController
import br.com.douglasmotta.data.response.ConversationResponse
import br.com.douglasmotta.data.response.ConversationsPaginatedResponse
import br.com.douglasmotta.extension.getCurrentUserId
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.conversation(conversationController: ConversationController) {
    authenticate {
        get("/conversations", {
            description = "Get all conversations of a user"
            request {
                pathParameter<String>("userId") {
                    description = "The id of the user"
                }
                queryParameter<String>("offset") {
                    description = "Offset"
                    required = false
                    allowEmptyValue = true
                }
                queryParameter<String>("limit") {
                    description = "Limit"
                    required = false
                    allowEmptyValue = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Success"
                    body<ConversationsPaginatedResponse>()
                }
                HttpStatusCode.Unauthorized
            }
        }) {
            val currentUserId = call.getCurrentUserId()

            currentUserId?.let { userId ->
                val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

                val conversations = conversationController.getConversationsBy(
                    userId = userId.toInt(),
                    offset = offset,
                    limit = limit,
                )
                call.respond(conversations)
            } ?: call.respond(HttpStatusCode.Unauthorized)
        }

        get("/conversations/{firstId}/{secondId}", {
            description = "Find a conversation by first and second ids"
            response {
                HttpStatusCode.OK to {
                    description = "Success"
                    body<ConversationResponse>()
                }
                HttpStatusCode.BadRequest to {
                    description = "Missing firstId or secondId in the path"
                }
                HttpStatusCode.NotFound to {
                    description = "Conversation not found"
                }
            }
        }) {
            val firstId = call.parameters["firstId"]
            val secondId = call.parameters["secondId"]

            if (firstId == null || secondId == null) {
                call.respond(HttpStatusCode.BadRequest.copy(description = "Missing firstId or secondId in the path"))
                return@get
            }

            val conversation = conversationController.findConversationBy(firstId.toInt(), secondId.toInt())
            conversation?.let {
                call.respond(it)
            } ?: call.respond(HttpStatusCode.NotFound.copy(description = "Conversation not found"))
        }
    }
}