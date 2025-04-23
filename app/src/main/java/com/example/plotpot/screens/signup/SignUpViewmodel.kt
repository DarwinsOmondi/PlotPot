package com.example.plotpot.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plotpot.models.SignUpUiState
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

class SignUpViewModel(private val supabase: SupabaseClient) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<SignUpUiState>>(UiState.Initial)
    val uiState: StateFlow<UiState<SignUpUiState>> = _uiState

    // Sign up user with email, password, and username
    fun signUpUser(email: String, password: String, username: String) {
        // Basic validation
        if (!isValidEmail(email)) {
            _uiState.value = UiState.Error("Invalid email format")
            return
        }
        if (password.length < 6) {
            _uiState.value = UiState.Error("Password must be at least 6 characters")
            return
        }
        if (username.isBlank()) {
            _uiState.value = UiState.Error("Username cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Sign up with Supabase Auth
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

                // Fetch the authenticated user
                val authUser = supabase.auth.currentUserOrNull()
                if (authUser == null) {
                    _uiState.value =
                        UiState.Error("Sign-up failed: Unable to retrieve user information")
                    return@launch
                }

                // Update the username in the profiles table (created by the trigger)
                val userId =
                    UUID.fromString(authUser.id) // Convert Supabase user ID (String) to UUID
                try {
                    supabase.from("profiles").update(
                        mapOf(
                            "username" to username
                        )
                    ) {
                        filter { eq("id", userId) }
                    }
                } catch (e: Exception) {
                    _uiState.value =
                        UiState.Error("Sign-up succeeded, but failed to update username: ${e.message}")
                    return@launch
                }

                // Map the Supabase user to your custom User model
                val user = User(
                    id = userId,
                    email = authUser.email ?: email,
                    username = username
                )
                _uiState.value = UiState.Success(SignUpUiState(user))
            } catch (e: Exception) {
                // Handle specific Supabase Auth errors
                val errorMessage = when {
                    e.message?.contains("unexpected_failure") == true -> {
                        "Sign-up failed: There was an issue with the database. Please try again later or contact support."
                    }

                    e.message?.contains("duplicate key") == true -> {
                        "Sign-up failed: This email is already registered. Please sign in or use a different email."
                    }

                    e.message?.contains("permission denied") == true -> {
                        "Sign-up failed: Database permission issue. Please contact support."
                    }

                    else -> {
                        "Sign-up failed: ${e.message}"
                    }
                }
                _uiState.value = UiState.Error(errorMessage)
            }
        }
    }

    // Basic email validation
    private fun isValidEmail(email: String): Boolean {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex())
    }
}