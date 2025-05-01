package com.example.plotpot.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plotpot.models.Contribution
import com.example.plotpot.models.ContributionUiState
import com.example.plotpot.models.UiState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.UUID

class ContributionViewModel(private val supabase: SupabaseClient) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ContributionUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ContributionUiState>> = _uiState

    //fetch story contribution by id
    fun fetchContributionsForStory(storyId: Long) {
        viewModelScope.launch {
            try {
                val contributions = supabase.from("contributions")
                    .select { filter { Contribution::storyId eq storyId } }
                    .decodeList<Contribution>()
                _uiState.value = UiState.Success(ContributionUiState(contributions.firstOrNull()))
            } catch (e: Exception) {
                Log.e("ContributionViewModel", "Failed to fetch contributions: ${e.message}")
                _uiState.value = UiState.Error("Failed to fetch contributions: ${e.message}")
            }
        }
    }

    //fetch recent user story contributions
    fun fetchRecentContributions() {
        viewModelScope.launch {
            try {
                val recentContributions =
                    supabase.from("contributions").select().decodeList<Contribution>()
                _uiState.value =
                    UiState.Success(ContributionUiState(contributions = recentContributions))
            } catch (e: Exception) {
                Log.e("ContributionViewModel", "Failed to fetch recent contributions: ${e.message}")
            }
        }
    }

    //add a story contribution
    @RequiresApi(Build.VERSION_CODES.O)
    fun addContribution(storyId: Long, userId: UUID, sentence: String) {
        viewModelScope.launch {
            try {
                val contribution = Contribution(
                    id = 0, // Auto-generated
                    storyId = storyId,
                    userId = userId,
                    sentence = sentence,
                    createdAt = OffsetDateTime.now()
                )
                supabase.from("contributions").insert(contribution)
                _uiState.value = UiState.Success(ContributionUiState(contribution))
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to add contribution: ${e.message}")
            }
        }
    }
}