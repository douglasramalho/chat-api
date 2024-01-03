package br.com.douglasmotta.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream

class FirebaseInitialization {

    fun initialization(): FirebaseApp {
        return try {
            val serviceAccount = FileInputStream("/env/serviceAccountKey.json")
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            FirebaseApp.initializeApp(options)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}