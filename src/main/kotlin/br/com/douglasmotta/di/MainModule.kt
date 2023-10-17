package br.com.douglasmotta.di

import br.com.douglasmotta.controller.*
import br.com.douglasmotta.data.*
import br.com.douglasmotta.security.hashing.HashingService
import br.com.douglasmotta.security.hashing.SHA256HashingService
import br.com.douglasmotta.security.token.JwtTokenService
import br.com.douglasmotta.security.token.TokenService
import org.koin.dsl.module

val mainModule = module {
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
        AuthController(get())
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