package br.com.douglasmotta.routes

import br.com.douglasmotta.controller.UserController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.users(userController: UserController) {
    authenticate {
        get("/users") {
            try {

                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)
                val users = userController.getUsers().filter { it.id != userId?.toInt() }

                if (users.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound, "No users found")
                    return@get
                }

                call.respond(users)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e)
            }
        }

        get("/users/{id}") {
            try {
                val userId = call.parameters["id"] ?: throw IllegalArgumentException("id missing")
                val user = userController.getUserBy(userId.toInt())

                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, "No user found")
                    return@get
                }

                call.respond(user)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e)
            }
        }
    }
}

fun Route.user() {

}