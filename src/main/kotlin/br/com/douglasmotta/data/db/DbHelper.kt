package br.com.douglasmotta.data.db

import br.com.douglasmotta.data.db.table.Conversations
import br.com.douglasmotta.data.db.table.Messages
import br.com.douglasmotta.data.db.table.Users
import io.ktor.server.application.*
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

object DbHelper {

    private var dbUrl = ""
    private var dbUser = ""
    private var dbPwd = ""

    fun Application.configureDbVariables() {
        dbUrl = System.getenv("DB_URL")
        dbUser = System.getenv("DB_USER")
        dbPwd = System.getenv("DB_PASSWORD")
    }

    fun database() = Database.connect(
        url = dbUrl,
        user = dbUser,
        password = dbPwd
    )
}

val Database.users get() = this.sequenceOf(Users)
val Database.messages get() = this.sequenceOf(Messages)
val Database.conversations get() = this.sequenceOf(Conversations)