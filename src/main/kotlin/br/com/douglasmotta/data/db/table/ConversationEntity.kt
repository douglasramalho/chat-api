package br.com.douglasmotta.data.db.table

import br.com.douglasmotta.data.response.ConversationResponse
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.timestamp
import java.time.Instant

interface ConversationEntity : Entity<ConversationEntity> {
    companion object : Entity.Factory<ConversationEntity>()
    val id: Int
    var firstMember: UserEntity
    var secondMember: UserEntity
    var createdAt: Instant
    var updatedAt: Instant
}

object Conversations: Table<ConversationEntity>("conversations") {

    val id = int("id").primaryKey().bindTo { it.id }
    val firstMemberId = int("first_member_id").references(Users) { it.firstMember }
    val secondMemberId = int("second_member_id").references(Users) { it.secondMember }
    val createdAt = timestamp("created_at").bindTo { it.createdAt }
    val updatedAt = timestamp("updated_at").bindTo { it.updatedAt }
}

fun ConversationEntity.toResponse(lastMessage: String?, unreadCount: Int) = ConversationResponse(
    id = this.id,
    members = listOf(
        this.firstMember.toResponse(),
        this.secondMember.toResponse(),
    ),
    unreadCount = unreadCount,
    createdAt = this.createdAt.toEpochMilli(),
    updatedAt = this.updatedAt.toEpochMilli(),
    lastMessage = lastMessage
)