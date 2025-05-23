package com.example.plotpot.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plotpot.models.Profile
import com.example.plotpot.models.ProfileUiState
import com.example.plotpot.models.UiState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ProfileViewModel(private val supabase: SupabaseClient) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ProfileUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ProfileUiState>> = _uiState

    fun fetchProfile(userId: UUID) {
        viewModelScope.launch {
            try {
                val profile = supabase.from("profiles")
                    .select { filter { Profile::id eq userId } }
                    .decodeSingleOrNull<Profile>()
                _uiState.value = UiState.Success(ProfileUiState(profile))
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
                _uiState.value = UiState.Error(userMessage)
            }
        }
    }

    fun updateProfile(userId: UUID, username: String?, bio: String?) {
        viewModelScope.launch {
            try {
                val updatedProfile = supabase.from("profiles")
                    .update(
                        mapOf(
                            "username" to username,
                            "bio" to bio
                        )
                    ) {
                        filter { Profile::id eq userId }
                    }
                    .decodeSingle<Profile>()
                _uiState.value = UiState.Success(ProfileUiState(updatedProfile))
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to update profile: ${e.message}")
            }
        }
    }
}