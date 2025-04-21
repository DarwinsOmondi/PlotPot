package com.example.plotpot.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plotpot.models.StoriesUiState
import com.example.plotpot.models.Story
import com.example.plotpot.models.UiState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.UUID

class StoryViewModel(private val supabase: SupabaseClient) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<StoriesUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<StoriesUiState>> = _uiState

    fun fetchStories(isCompleted: Boolean = true) {
        viewModelScope.launch {
            try {
                val stories = supabase.from("stories")
                    .select { filter { Story::isCompleted eq isCompleted } }
                    .decodeList<Story>()
                _uiState.value = UiState.Success(StoriesUiState(stories))
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to fetch stories: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createStory(userId: UUID, title: String, description: String?, totalSentences: Int) {
        viewModelScope.launch {
            try {
                val newStory = Story(
                    id = 0, // Will be auto-generated
                    title = title,
                    description = description,
                    createdBy = userId,
                    createdAt = OffsetDateTime.now(),
                    isCompleted = false,
                    totalSentences = totalSentences
                )
                supabase.from("stories").insert(newStory)
                fetchStories() // Refresh the list
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to create story: ${e.message}")
            }
        }
    }
}