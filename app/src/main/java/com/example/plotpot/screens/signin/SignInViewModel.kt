package com.example.plotpot.screens.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plotpot.models.SignInUiState
import com.example.plotpot.models.UiState
import com.example.plotpot.models.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SignInViewModel(private val supabase: SupabaseClient) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<SignInUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<SignInUiState>> = _uiState

    // Sign in user with email and password
    fun signInUser(email: String, password: String) {
        // Basic validation
        if (!isValidEmail(email)) {
            _uiState.value = UiState.Error("Invalid email format")
            return
        }
        if (email.isBlank()) {
            _uiState.value = UiState.Error("Email cannot be empty")
            return
        }
        if (password.length < 6) {
            _uiState.value = UiState.Error("Password must be at least 6 characters")
            return
        }
        if (password.isBlank()) {
            _uiState.value = UiState.Error("Password cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                // Sign in with Supabase Auth
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                // Fetch the authenticated user
                val authUser = supabase.auth.currentUserOrNull()
                if (authUser == null) {
                    _uiState.value =
                        UiState.Error("Sign-in failed: Unable to retrieve user information")
                    return@launch
                }

                // Fetch the username from the profiles table
                val userId =
                    UUID.fromString(authUser.id) // Convert Supabase user ID (String) to UUID
                val profile = supabase.from("profiles")
                    .select { filter { eq("id", userId) } }
                    .decodeSingleOrNull<Map<String, Any>>()

                val username = profile?.get("username") as? String ?: "Unknown"

                // Map the Supabase user to your custom User model
                val user = User(
                    id = userId,
                    email = authUser.email ?: email,
                    username = username
                )
                _uiState.value = UiState.Success(SignInUiState(user))
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Sign-in failed: ${e.message}")
            }
        }
    }

    // Basic email validation
    private fun isValidEmail(email: String): Boolean {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex())
    }
}