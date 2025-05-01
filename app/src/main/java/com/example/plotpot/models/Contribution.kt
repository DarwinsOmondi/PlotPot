package com.example.plotpot.models

import com.example.plotpot.customs.OffsetDateTimeSerializer
import com.example.plotpot.customs.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class Contribution(
    val id: Long, // bigint
    val storyId: Long, // bigint (references stories.id)
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID, // uuid (references auth.users.id)
    val sentence: String, // varchar(280)
    @Serializable(with = OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime // timestamp with time zone
)

//data class ContributionWithDetails(
//    val id: Long,
//    val storyId: Long,
//    val userId: UUID,
//    val sentence: String,
//    val createdAt: OffsetDateTime,
//    val storyTitle: String,
//    val username: String
//)