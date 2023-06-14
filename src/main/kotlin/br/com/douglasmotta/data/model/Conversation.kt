package br.com.douglasmotta.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Conversation(
    @BsonId
    val id: String = ObjectId().toString(),
    val members: ArrayList<String>,
    val timestamp: Long
)