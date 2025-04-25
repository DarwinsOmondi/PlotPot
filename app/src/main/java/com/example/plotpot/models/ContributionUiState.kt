package com.example.plotpot.models

import java.time.OffsetDateTime
import java.util.UUID


data class ContributionUiState(
    val contribution: Contribution? = null,
    val contributions: List<ContributionWithDetails> = emptyList()
)

data class ContributionWithDetails(
    val id: Long,
    val storyId: Long,
    val userId: UUID,
    val sentence: String,
    val createdAt: OffsetDateTime,
    val storyTitle: String,
    val username: String
)