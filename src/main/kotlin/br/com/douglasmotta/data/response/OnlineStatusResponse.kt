package br.com.douglasmotta.data.response

import kotlinx.serialization.Serializable

@Serializable
data class OnlineStatusResponse(
   val activeUserIds: List<Int>
)