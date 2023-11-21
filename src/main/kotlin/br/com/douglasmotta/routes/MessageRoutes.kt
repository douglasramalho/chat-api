package br.com.douglasmotta.routes

import br.com.douglasmotta.controller.MessageController
import br.com.douglasmotta.data.response.MessageResponse
import br.com.douglasmotta.data.response.MessagesPaginatedResponse
import br.com.douglasmotta.extension.getCurrentUserId
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.message(messageController: MessageController) {
    authenticate {
        get(
            path = "/messages/{receiverId}", {
                description = "Get messages of a conservation"
                response {
                    HttpStatusCode.OK to {
                        description = "Success"
                        body<MessagesPaginatedResponse>()
                    }
                    HttpStatusCode.Unauthorized
                    HttpStatusCode.BadRequest to {
                        description = "Missing receiverId in the path"
                    }
                }
            }) {
            val senderId = call.getCurrentUserId()
            val receiverId = call.parameters["receiverId"]

            if (senderId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            if (receiverId == null) {
                call.respond(HttpStatusCode.BadRequest.copy(description = "Missing senderId or receiverId in the path"))
                return@get
            }

            val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

            try {
                val messages = messageController.getMessagesBy(
                    senderId.toInt(),
                    receiverId.toInt(),
                    offset,
                    limit,
                )
                call.respond(messages)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e)
            }
        }
    }
}