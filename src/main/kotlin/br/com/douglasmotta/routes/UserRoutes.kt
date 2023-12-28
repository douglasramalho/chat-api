package br.com.douglasmotta.routes

import br.com.douglasmotta.controller.UserController
import br.com.douglasmotta.data.response.UserResponse
import br.com.douglasmotta.extension.getCurrentUserId
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.users(userController: UserController) {
    authenticate {
        get("/users", {
            description = "Get users for start a conversation"
            response {
                HttpStatusCode.OK to {
                    description = "Success"
                    body<List<UserResponse>>()
                }
                HttpStatusCode.Unauthorized
                HttpStatusCode.NotFound to {
                    description = "There are no users to start a conversation with"
                }
            }
        }) {
            try {
                val userId = call.getCurrentUserId() ?: kotlin.run {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }
                val users = userController.getUsers().filter { it.id != userId.toInt() }

                if (users.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound, "No users found")
                    return@get
                }

                call.respond(users)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e)
            }
        }

        get("/users/{id}", {
            description = "Get a specific user by id"
            response {
                HttpStatusCode.OK to {
                    description = "Success"
                    body<UserResponse>()
                }
                HttpStatusCode.Unauthorized
                HttpStatusCode.NotFound to {
                    description = "User with the provided id not found"
                }
            }
        }) {
            try {
                val userId = call.parameters["id"] ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest.copy(description = "Missing id"))
                    return@get
                }
                val user = userController.getUserBy(userId.toInt())

                if (user == null) {
                    call.respond(HttpStatusCode.NotFound.copy(description = "User with the provided id not found"))
                    return@get
                }

                call.respond(user)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e)
            }
        }
    }
}