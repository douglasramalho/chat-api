package br.com.douglasmotta.plugins

import br.com.douglasmotta.room.RoomController
import br.com.douglasmotta.routes.chatSocket
import br.com.douglasmotta.routes.getAllMessages
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val roomController by inject<RoomController>()
    install(Routing) {
        chatSocket(roomController)
        getAllMessages(roomController)
    }
}
