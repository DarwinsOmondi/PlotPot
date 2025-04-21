package com.example.plotpot.models

// Generic UI state for all ViewModels
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// Specific UI states for each feature
data class ProfileUiState(val profile: Profile? = null)
data class StoriesUiState(val stories: List<Story> = emptyList())
data class ContributionUiState(val contribution: Contribution? = null)
data class VoteUiState(val votes: List<Vote> = emptyList())
data class AnimationUiState(val animation: Animation? = null)
data class ChallengeUiState(val challenges: List<Challenge> = emptyList())