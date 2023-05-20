package br.com.douglasmotta

import br.com.douglasmotta.di.mainModule
import io.ktor.server.application.*
import br.com.douglasmotta.plugins.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}
