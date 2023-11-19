package br.com.douglasmotta.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.server.application.*

fun Application.configureSwagger() {
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger-ui"
            forwardRoot = true
        }
        info {
            title = "Chat API"
            version = "1.0"
            description = "Swagger for Chat API"
        }
        server {
            url = "localhost:8080/swagger-ui"
            description = "Development server"
        }
    }
}