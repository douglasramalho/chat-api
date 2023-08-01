package br.com.douglasmotta.di

import br.com.douglasmotta.controller.ChatController
import br.com.douglasmotta.data.*
import br.com.douglasmotta.controller.ConversationController
import br.com.douglasmotta.controller.MessageController
import br.com.douglasmotta.controller.UserController
import br.com.douglasmotta.security.hashing.HashingService
import br.com.douglasmotta.security.hashing.SHA256HashingService
import br.com.douglasmotta.security.token.JwtTokenService
import br.com.douglasmotta.security.token.TokenService
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    val mongoPassword = System.getenv("MONGO_PASSWORD")
    single {
        KMongo.createClient("mongodb+srv://douglas1102rm:$mongoPassword@cluster0.gha7dqu.mongodb.net/chat?retryWrites=majority")
            .coroutine
            .getDatabase("chat")
    }

    single<UserLocalDataSource> {
        UserDbLocalDataSourceImpl()
    }

    single<TokenService> {
        JwtTokenService()
    }

    single<HashingService> {
        SHA256HashingService()
    }

    single<MessageLocalDataSource> {
        MessageDbDataSourceImpl()
    }

    single<ConversationLocalDataSource> {
        ConversationDbDataSourceImpl()
    }

    single {
        UserController(get())
    }

    single {
        ConversationController(get(), get())
    }

    single {
        ChatController(get(), get(), get())
    }

    single {
        MessageController(get())
    }
}