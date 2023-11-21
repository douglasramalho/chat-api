package br.com.douglasmotta.routes

import br.com.douglasmotta.controller.AuthController
import br.com.douglasmotta.controller.UserController
import br.com.douglasmotta.data.request.AuthUserRequest
import br.com.douglasmotta.data.request.CreateUserRequest
import br.com.douglasmotta.data.response.AuthResponse
import br.com.douglasmotta.data.response.UserResponse
import br.com.douglasmotta.extension.getCurrentUserId
import br.com.douglasmotta.security.hashing.HashingService
import br.com.douglasmotta.security.hashing.SaltedHash
import br.com.douglasmotta.security.token.TokenClaim
import br.com.douglasmotta.security.token.TokenConfig
import br.com.douglasmotta.security.token.TokenService
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userController: AuthController,
) {
    post("signup", {
        description = "Create a new user"
        request {
            body(CreateUserRequest::class)
        }
        response {
            HttpStatusCode.OK to {
                description = "Success"
            }
            HttpStatusCode.BadRequest to {
                description =
                    "Username, password, first name and last name are mandatory. Password should have at least 8 characters."
            }
            HttpStatusCode.Conflict to {
                description = "User with the provided e-mail already exists. Please choose a different one."
            }
        }
    }) {
        val request = call.receiveNullable<CreateUserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank =
            request.username.isBlank() || request.password.isBlank() || request.firstName.isBlank() || request.lastName.isBlank()
        val isPasswordTooShort = request.password.length < 8
        if (areFieldsBlank || isPasswordTooShort) {
            call.respond(
                HttpStatusCode.BadRequest.copy(
                    description = "Username, password, first name and last name are mandatory. Password should have at least 8 characters."
                )
            )
            return@post
        }

        val existentUserWithUsername = userController.getUserByUsername(request.username)
        if (existentUserWithUsername != null) {
            call.respond(
                HttpStatusCode.Conflict.copy(
                    description = "User with the provided e-mail already exists. Please choose a different one."
                )
            )
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)

        val wasAcknowledged = userController.insertUser(request, saltedHash.hash, saltedHash.salt)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    hashingService: HashingService,
    authController: AuthController,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
) {
    post("signin", {
        description = "Sign-in"
        request {
            body(AuthUserRequest::class)
        }
        response {
            HttpStatusCode.OK to {
                description = "Success"
                body(AuthResponse::class)
            }
            HttpStatusCode.BadRequest
            HttpStatusCode.Conflict to {
                description = "Incorrect e-mail or password"
            }
        }
    }) {
        val request = call.receiveNullable<AuthUserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = authController.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict.copy(description = "Incorrect e-mail or password"))
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate(
    userController: UserController,
) {
    authenticate {
        get("authenticate", {
            description = "Get the info of the current authenticated user"
            response {
                HttpStatusCode.OK to {
                    description = "Success"
                    body<UserResponse>()
                }
                HttpStatusCode.Unauthorized
            }
        }) {
            val userId = call.getCurrentUserId()
            val userResponse = userId?.let {
                userController.getUserBy(it.toInt())
            }

            userResponse?.let {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = userResponse
                )
                return@get
            }

            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}