package com.example.plotpot.viewmodels

import android.os.Build
import android.util.Log
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.UUID

class StoryViewModel(private val supabase: SupabaseClient) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<StoriesUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<StoriesUiState>> = _uiState

    private val _storyMap = MutableStateFlow<Map<Long, Story>>(emptyMap())
    val storyMap: StateFlow<Map<Long, Story>> = _storyMap.asStateFlow()


    fun fetchStories(isCompleted: Boolean = true) {
        viewModelScope.launch {
            try {
                val stories = supabase.from("stories")
                    .select { filter { Story::isCompleted eq isCompleted } }
                    .decodeList<Story>()
                _uiState.value = UiState.Success(StoriesUiState(stories))
                Log.e("StoryViewModel", "Error fetching Stories $stories")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to fetch stories: ${e.message}")
                Log.e("StoryViewModel", "Failed to fetch stories: ${e.message}")
            }
        }
    }

    fun fetchStoryById(storyId: Long) {
        viewModelScope.launch {
            try {
                val story = supabase.from("stories").select { filter { Story::id eq storyId } }
                    .decodeAs<Story>()
                Log.e("StoryViewModel", "Failed to create story: $story")
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Failed to fetch by storyId: ${e.message}")
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
                Log.e("StoryViewModel", "Failed to create story: ${e.message}")
            }
        }
    }
}