package br.com.douglasmotta.di

import br.com.douglasmotta.data.MessageLocalDataSource
import br.com.douglasmotta.data.MessageLocalDataSourceImpl
import br.com.douglasmotta.room.RoomController
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        KMongo.createClient("mongodb+srv://douglas1102rm:Z57nLKwjbEEatfjn@cluster0.gha7dqu.mongodb.net/")
            .coroutine
            .getDatabase("chat")
    }

    single<MessageLocalDataSource> {
        MessageLocalDataSourceImpl(get())
    }

    single {
        RoomController(get())
    }
}