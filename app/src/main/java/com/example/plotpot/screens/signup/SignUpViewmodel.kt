package com.example.plotpot.screens.signup

import android.util.Log
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
    private val _uiState =
        MutableStateFlow<UiState<SignUpUiState>>(UiState.Success(SignUpUiState()))
    val uiState: StateFlow<UiState<SignUpUiState>> = _uiState

    // Sign up user with email, password, and username
    fun signUpUser(email: String, password: String, username: String) {
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

        if (username.isBlank()) {
            _uiState.value = UiState.Error("Username cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                // Sign up with Supabase Auth
                val authUser = supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

                // After successful sign-up, create a profile entry
                val userId =
                    UUID.fromString(authUser?.id) // Convert Supabase user ID (String) to UUID
                supabase.from("profiles").insert(
                    mapOf(
                        "id" to userId,
                        "username" to username,
                        "avatar_url" to null,
                        "bio" to null
                    )
                )

                // Map the Supabase user to your custom User model
                val user = User(
                    id = userId,
                    email = authUser?.email ?: email,
                    username = username
                )
                _uiState.value = UiState.Success(SignUpUiState(user))
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Sign-up failed: ${e.message}")
                Log.e("SignUpViewModel", "Sign-up failed", e)
            }
        }
    }

    // Basic email validation
    private fun isValidEmail(email: String): Boolean {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex())
    }
}