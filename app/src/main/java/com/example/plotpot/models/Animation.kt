package com.example.plotpot.models

import com.example.plotpot.customs.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.time.OffsetDateTime

@Serializable
data class Animation(
    val id: Long, // bigint
    val storyId: Long, // bigint (references stories.id)
    val videoUrl: String, // text
    @Serializable(with = OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime, // timestamp with time zone
    val metadata: JsonElement? // jsonb, nullable
)
