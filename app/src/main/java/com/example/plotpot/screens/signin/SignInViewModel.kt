package com.example.plotpot.screens.signin

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plotpot.models.SignInUiState
import com.example.plotpot.models.UiState
import com.example.plotpot.models.User
import com.example.plotpot.models.Profile // Import the new Profile class
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SignInViewModel(private val supabase: SupabaseClient) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<SignInUiState>>(UiState.Initial)
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

                // Fetch the profile from the profiles table
                val userId =
                    UUID.fromString(authUser.id) // Convert Supabase user ID (String) to UUID
                val profile = supabase.from("profiles")
                    .select { filter { eq("id", userId) } }
                    .decodeSingleOrNull<Profile>() // Deserialize into Profile class

                if (profile == null) {
                    _uiState.value = UiState.Error("Sign-in failed: Profile not found")
                    return@launch
                }

                // Map the Supabase user to your custom User model
                val user = User(
                    id = userId,
                    email = authUser.email ?: email,
                    username = profile.username
                )
                _uiState.value = UiState.Success(SignInUiState(user))
                Log.d("SignInViewModel", "Sign-in successful: $user")
            } catch (e: Exception) {
                Log.e("SignInViewModel", "Sign-in failed", e)
                _uiState.value = UiState.Error("Sign-in failed: ${e.message}")
            }
        }
    }

    // Basic email validation
    private fun isValidEmail(email: String): Boolean {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex())
    }
    fun saveUserLoggedInStates(context: Context, isLoggedIn: Boolean) {
        val sharedPreferences = context.getSharedPreferences("UrbanGo", Context.MODE_PRIVATE)
        sharedPreferences.edit() { putBoolean("isLoggedIn", isLoggedIn) }
    }
}