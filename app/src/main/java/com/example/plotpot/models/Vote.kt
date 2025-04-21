package com.example.plotpot.models

import com.example.plotpot.customs.OffsetDateTimeSerializer
import com.example.plotpot.customs.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class Vote(
    val id: Long, // bigint
    val storyId: Long, // bigint (references stories.id)
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID, // uuid (references auth.users.id)
    val plotTwistOption: String, // text
    @Serializable(with = OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime // timestamp with time zone
)
