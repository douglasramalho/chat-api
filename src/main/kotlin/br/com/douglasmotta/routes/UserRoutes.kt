package br.com.douglasmotta.routes

import br.com.douglasmotta.controller.UserController
import br.com.douglasmotta.data.response.ImageResponse
import br.com.douglasmotta.data.response.UserResponse
import br.com.douglasmotta.extension.getCurrentUserId
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.users(userController: UserController) {

    post("/profile-picture", {
        description = "Upload the user profile picture"
        request {
            multipartBody {
                this.part<File>("profilePicture")
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "Success"
                body<ImageResponse>()
            }
            HttpStatusCode.BadRequest to {
                description =
                    "No image file provided or file size more than 10MB or image format not supported (png, jpg, jpeg)"
            }
        }
    }) {
        val multipartData = call.receiveMultipart()
        try {
            var file: File? = null
            var fileBytes: ByteArray? = null
            var fileSize = 0

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName as String
                        fileSize = call.request.header(HttpHeaders.ContentLength)?.toIntOrNull() ?: 0


                        fileBytes = part.streamProvider().readBytes()
                        file = File("uploads/$fileName")
                    }

                    else -> {}
                }
            }

            file?.let {
                if (!listOf("png", "jpg", "jpeg").contains(it.extension)) {
                    call.respond(HttpStatusCode.BadRequest.copy(description = "Image should be one of the following types: png, jpg, jpeg"))
                    return@post
                }

                if (fileSize > 10_000_000) {
                    call.respond(HttpStatusCode.BadRequest.copy(description = "Image size should be lower or equal than 10MB"))
                    return@post
                }

                fileBytes?.let { bytes ->
                    it.writeBytes(bytes)
                }

                val response = userController.uploadImage(it)

                call.respond(response)
            } ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest.copy(description = "No image file provided"))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e)
        }

    }

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