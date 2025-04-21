package com.example.plotpot.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plotpot.models.Animation
import com.example.plotpot.models.AnimationUiState
import com.example.plotpot.models.UiState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import java.time.OffsetDateTime

class AnimationViewModel(private val supabase: SupabaseClient) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<AnimationUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<AnimationUiState>> = _uiState

    fun fetchAnimationForStory(storyId: Long) {
        viewModelScope.launch {
            try {
                val animation = supabase.from("animations")
                    .select { filter { Animation::storyId eq storyId } }
                    .decodeSingleOrNull<Animation>()
                _uiState.value = UiState.Success(AnimationUiState(animation))
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to fetch animation: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createAnimation(storyId: Long, videoUrl: String, metadata: Map<String, Any>?) {
        viewModelScope.launch {
            try {
                val animation = Animation(
                    id = 0, // Auto-generated
                    storyId = storyId,
                    videoUrl = videoUrl,
                    createdAt = OffsetDateTime.now(),
                    metadata = metadata as JsonElement?
                )
                supabase.from("animations").insert(animation)
                fetchAnimationForStory(storyId) // Refresh
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to create animation: ${e.message}")
            }
        }
    }
}