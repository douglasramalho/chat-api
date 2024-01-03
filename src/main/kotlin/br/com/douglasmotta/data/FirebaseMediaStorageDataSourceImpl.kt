package br.com.douglasmotta.data

import br.com.douglasmotta.firebase.FirebaseInitialization
import com.google.cloud.storage.Acl
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.firebase.cloud.StorageClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class FirebaseMediaStorageDataSourceImpl(
    firebaseInitialization: FirebaseInitialization,
) : MediaStorageDataSource {

    private val client = StorageClient.getInstance(firebaseInitialization.initialization())

    override suspend fun storeProfilePicture(file: File): String {
        return try {
            withContext(Dispatchers.IO) {
                val acls = listOf(
                    Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)
                )
                val blobId = BlobId.of("droidchat-e0397.appspot.com", "profilePicture/${file.name}")
                val blobInfo: BlobInfo = BlobInfo
                    .newBuilder(blobId)
                    .setContentType("image/${file.extension}")
                    .setAcl(acls)
                    .build()

                val blob = client.bucket("droidchat-e0397.appspot.com")
                    .storage
                    .create(
                        blobInfo,
                        Files.readAllBytes(
                            Paths.get(file.path)
                        )
                    )

                blob.mediaLink
            }
        } catch (e: Exception) {
            throw e
        }
    }
}