package br.com.douglasmotta.plugins

import br.com.douglasmotta.controller.*
import br.com.douglasmotta.routes.*
import br.com.douglasmotta.security.hashing.HashingService
import br.com.douglasmotta.security.token.TokenConfig
import br.com.douglasmotta.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(tokenConfig: TokenConfig) {
    val hashingService by inject<HashingService>()
    val tokenService by inject<TokenService>()

    val authController by inject<AuthController>()
    val userController by inject<UserController>()
    val conversationController by inject<ConversationController>()
    val chatController by inject<ChatController>()
    val messageController by inject<MessageController>()
    install(Routing) {
        users(userController)
        signUp(hashingService, authController)
        signIn(hashingService, authController, tokenService, tokenConfig)
        authenticate(userController)
        getSecretInfo()
        chatSocket(chatController)
        conversation(conversationController)
        message(messageController)
    }
}
