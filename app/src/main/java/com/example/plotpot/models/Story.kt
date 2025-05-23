package com.example.plotpot.models

import com.example.plotpot.customs.OffsetDateTimeSerializer
import com.example.plotpot.customs.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class Story(
    val title: String, // text
    val description: String?, // varchar(1000), nullable
    @Serializable(with = UUIDSerializer::class)
    val createdBy: UUID, // uuid (references auth.users.id)
    val isCompleted: Boolean, // boolean
    val totalSentences: Int // integer
)