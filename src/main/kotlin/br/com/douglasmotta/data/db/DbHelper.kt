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
        dbUrl = environment.config.propertyOrNull("db.config.db_url")?.getString() ?: ""
        dbUser = environment.config.propertyOrNull("db.config.db_user")?.getString() ?: ""
        dbPwd = environment.config.propertyOrNull("db.config.db_pwd")?.getString() ?: ""
    }

    fun database() = Database.connect(
        dbUrl,
        user = dbUser,
        password = dbPwd
    )
}

val Database.users get() = this.sequenceOf(Users)
val Database.messages get() = this.sequenceOf(Messages)
val Database.conversations get() = this.sequenceOf(Conversations)