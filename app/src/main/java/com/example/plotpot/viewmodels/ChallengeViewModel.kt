package com.example.plotpot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plotpot.models.Challenge
import com.example.plotpot.models.ChallengeUiState
import com.example.plotpot.models.UiState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class ChallengeViewModel(private val supabase: SupabaseClient) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ChallengeUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ChallengeUiState>> = _uiState

    fun fetchChallenges() {
        viewModelScope.launch {
            try {
                val challenges = supabase.from("challenges")
                    .select { filter { Challenge::isActive eq true } }
                    .decodeList<Challenge>()
                _uiState.value = UiState.Success(ChallengeUiState(challenges))
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to fetch challenges: ${e.message}")
            }
        }
    }

    fun createChallenge(
        title: String,
        description: String?,
        startDate: OffsetDateTime,
        endDate: OffsetDateTime
    ) {
        viewModelScope.launch {
            try {
                val challenge = Challenge(
                    id = 0, // Auto-generated
                    title = title,
                    description = description,
                    startDate = startDate,
                    endDate = endDate,
                    isActive = true
                )
                supabase.from("challenges").insert(challenge)
                fetchChallenges() // Refresh
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to create challenge: ${e.message}")
            }
        }
    }
}