package com.example.plotpot.models

import com.example.plotpot.customs.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class Challenge(
    val id: Long, // bigint
    val title: String, // text
    val description: String?, // varchar(1000), nullable
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startDate: OffsetDateTime, // timestamp with time zone
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endDate: OffsetDateTime, // timestamp with time zone
    val isActive: Boolean // boolean
)
