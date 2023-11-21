package br.com.douglasmotta.extension

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun ApplicationCall.getCurrentUserId(): String? {
    val principal = this.principal<JWTPrincipal>()
    return principal?.getClaim("userId", String::class)
}