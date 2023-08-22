package br.com.douglasmotta.routes

import br.com.douglasmotta.data.UserLocalDataSource
import br.com.douglasmotta.data.db.table.UserEntity
import br.com.douglasmotta.data.model.User
import br.com.douglasmotta.data.request.AuthUserRequest
import br.com.douglasmotta.data.request.CreateUserRequest
import br.com.douglasmotta.data.response.AuthResponse
import br.com.douglasmotta.security.hashing.HashingService
import br.com.douglasmotta.security.hashing.SaltedHash
import br.com.douglasmotta.security.token.TokenClaim
import br.com.douglasmotta.security.token.TokenConfig
import br.com.douglasmotta.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userLocalDataSource: UserLocalDataSource
) {
    post("signup") {
        val request = call.receiveNullable<CreateUserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank() || request.firstName.isBlank() || request.lastName.isBlank()
        val isPasswordTooShort = request.password.length < 8
        if (areFieldsBlank || isPasswordTooShort) {
            call.respond(HttpStatusCode.BadRequest, "Mandatory fields: Username, Password, First Name and Last Name. Password should have at least 8 chars.")
            return@post
        }

        val existentUserWithUsername = userLocalDataSource.getUserByUsername(request.username)
        if (existentUserWithUsername != null) {
            call.respond(HttpStatusCode.Conflict, "Username already exists. Please choose a different username.")
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)

        val userEntity = UserEntity {
            username = request.username
            password = saltedHash.hash
            salt = saltedHash.salt
            firstName = request.firstName
            lastName = request.lastName
            profilePictureUrl = request.profilePictureUrl
        }

        val wasAcknowledged = userLocalDataSource.insertUser(userEntity)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.signIn(
    hashingService: HashingService,
    userLocalDataSource: UserLocalDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
) {
    post("signin") {
        val request = call.receiveNullable<AuthUserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userLocalDataSource.getUserByUsername(request.username)
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
            call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
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

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}