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
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.UUID

class StoryViewModel(private val supabase: SupabaseClient) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<StoriesUiState>>(UiState.Initial)
    val uiState: StateFlow<UiState<StoriesUiState>> = _uiState.asStateFlow()

    private val _storyMap =
        MutableStateFlow<Map<Long, MutableStateFlow<UiState<Story>>>>(emptyMap())
    val storyMap: StateFlow<Map<Long, MutableStateFlow<UiState<Story>>>> = _storyMap.asStateFlow()

    private val _createUiState = MutableStateFlow<UiState<Unit>>(UiState.Initial)
    val createUiState: StateFlow<UiState<Unit>> = _createUiState.asStateFlow()
    init {
        fetchStory()
    }

    fun fetchStories(isCompleted: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val stories = supabase.from("stories")
                    .select { filter { eq("isCompleted", isCompleted) } }
                    .decodeList<Story>()
                val story = supabase.postgrest.from("stories").select().decodeSingle<Story>()
                _uiState.value = UiState.Success(StoriesUiState(stories))
                Log.d("StoryViewModel", "Fetched stories: $story")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to fetch stories: ${e.message}")
                Log.e("StoryViewModel", "Failed to fetch stories: ${e.message}")
            }
        }
    }

    fun fetchStory() {
        viewModelScope.launch {
            try {
                val story = supabase.postgrest.from("stories").select().decodeSingle<Story>()
                Log.d("StoryViewModel", "Fetched stories: $story")
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Failed to fetch stories: ${e.message}")
            }
        }
    }

    fun getStoryById(storyId: Long): StateFlow<UiState<Story>> {
        val existingFlow = _storyMap.value[storyId]
        if (existingFlow != null) {
            return existingFlow.asStateFlow()
        }

        val newFlow = MutableStateFlow<UiState<Story>>(UiState.Loading)
        _storyMap.value = _storyMap.value + (storyId to newFlow)
        viewModelScope.launch {
            try {
                val story = supabase.from("stories")
                    .select { filter { eq("id", "$storyId") } }
                    .decodeAs<Story>()
                newFlow.value = UiState.Success(story)
                Log.d("StoryViewModel", "Fetched story: $story")
            } catch (e: Exception) {
                val message = e.message ?: ""
                val userMessage = when {
                    message.contains(
                        "Unauthorized",
                        ignoreCase = true
                    ) -> "You are not logged in. PLease sign in."

                    message.contains(
                        "Forbidden",
                        ignoreCase = true
                    ) -> "You don't have permission to perform this action."

                    message.contains(
                        "timeout",
                        ignoreCase = true
                    ) || message.contains(
                        "unreachable",
                        ignoreCase = true
                    ) -> "Network issue .Please try again later."

                    message.contains(
                        "Internal Server Error",
                        ignoreCase = true
                    ) -> "A server error occurred. Try again later."

                    else -> "An expected error occurred : ${e.message}"
                }
                newFlow.value = UiState.Error(userMessage)
                Log.e("StoryViewModel", "Failed to fetch story: ${e.message}")
            }
        }
        return newFlow.asStateFlow()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createStory(title: String, description: String?, totalSentences: Int) {
        viewModelScope.launch {
            try {
                _createUiState.value = UiState.Loading
                val authUser = supabase.auth.currentUserOrNull()
                if (authUser == null) {
                    _createUiState.value = UiState.Error("User not logged in")
                    return@launch
                }
                val userId = UUID.fromString(authUser.id)
                // Create Story without specifying id
                val newStory = Story(
                    title = title,
                    description = description,
                    createdBy = userId,
                    isCompleted = false,
                    totalSentences = totalSentences
                )
                supabase.from("stories").insert(newStory)
                _createUiState.value = UiState.Success(Unit)
                fetchStories(isCompleted = false) // Refresh the list
                Log.d("StoryViewModel", "Story created successfully")
            } catch (e: Exception) {
                _createUiState.value = UiState.Error("Failed to create story: ${e.message}")
                Log.e("StoryViewModel", "Failed to create story: ${e.message}")
            }
        }
    }
}