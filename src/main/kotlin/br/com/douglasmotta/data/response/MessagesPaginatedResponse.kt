package br.com.douglasmotta.data.response

import kotlinx.serialization.Serializable

@Serializable
data class MessagesPaginatedResponse(
    val messages: List<MessageResponse>,
    val total: Int,
    val hasMore: Boolean,

)