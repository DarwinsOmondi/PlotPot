package com.example.plotpot.models

import com.example.plotpot.customs.OffsetDateTimeSerializer
import com.example.plotpot.customs.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class Story(
    val id: Long, // bigint
    val title: String, // text
    val description: String?, // varchar(1000), nullable
    @Serializable(with = UUIDSerializer::class)
    val createdBy: UUID, // uuid (references auth.users.id)
    @Serializable(with = OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime, // timestamp with time zone
    val isCompleted: Boolean, // boolean
    val totalSentences: Int // integer
)