package br.com.douglasmotta.plugins

import br.com.douglasmotta.session.ChatSession
import io.ktor.server.sessions.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.routing.*
import io.ktor.util.*

fun Application.configureSecurity() {

    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(Plugins) {
        if (call.sessions.get<ChatSession>() == null) {
            val username = call.parameters["username"] ?: "Guest"
            call.sessions.set(ChatSession(username, generateNonce()))
        }
    }
}
