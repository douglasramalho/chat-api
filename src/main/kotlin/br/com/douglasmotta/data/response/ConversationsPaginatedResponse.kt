package br.com.douglasmotta.data.response

import kotlinx.serialization.Serializable

@Serializable
data class ConversationsPaginatedResponse(
    val conversations: List<ConversationResponse>,
    val total: Int,
    val hasMore: Boolean,

)