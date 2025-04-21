package com.example.plotpot.models

import com.example.plotpot.customs.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Profile(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID, // UUID for user ID (matches auth.users.id)
    val username: String, // text
    val avatarUrl: String?, // text, nullable
    val bio: String? // varchar(500), nullable
)