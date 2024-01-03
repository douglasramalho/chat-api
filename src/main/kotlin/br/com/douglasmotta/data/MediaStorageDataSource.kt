package br.com.douglasmotta.data

import java.io.File

interface MediaStorageDataSource {

    suspend fun storeProfilePicture(file: File): String
}