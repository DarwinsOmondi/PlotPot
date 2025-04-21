package com.example.plotpot.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plotpot.models.Vote
import com.example.plotpot.models.UiState
import com.example.plotpot.models.VoteUiState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.UUID

class VoteViewModel(private val supabase: SupabaseClient) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<VoteUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<VoteUiState>> = _uiState

    fun fetchVotesForStory(storyId: Long) {
        viewModelScope.launch {
            try {
                val votes = supabase.from("votes")
                    .select { filter { Vote::storyId eq storyId } }
                    .decodeList<Vote>()
                _uiState.value = UiState.Success(VoteUiState(votes))
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to fetch votes: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun castVote(storyId: Long, userId: UUID, plotTwistOption: String) {
        viewModelScope.launch {
            try {
                val vote = Vote(
                    id = 0, // Auto-generated
                    storyId = storyId,
                    userId = userId,
                    plotTwistOption = plotTwistOption,
                    createdAt = OffsetDateTime.now()
                )
                supabase.from("votes").insert(vote)
                fetchVotesForStory(storyId) // Refresh votes
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to cast vote: ${e.message}")
            }
        }
    }
}