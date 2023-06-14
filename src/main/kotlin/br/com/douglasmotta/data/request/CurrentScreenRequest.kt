package br.com.douglasmotta.data.request

import kotlinx.serialization.Serializable

@Serializable
data class CurrentScreenRequest(
    val screenName: String,
    val conversationId: String?
)
