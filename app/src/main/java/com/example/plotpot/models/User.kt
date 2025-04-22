package com.example.plotpot.models

import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val username: String,
)
