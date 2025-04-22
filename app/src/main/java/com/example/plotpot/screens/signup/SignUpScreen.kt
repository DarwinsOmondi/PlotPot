package com.example.plotpot.screens.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plotpot.customs.PlotPotViewModelFactory
import com.example.plotpot.customs.supabase
import com.example.plotpot.models.UiState
import com.example.plotpot.R


@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpSuccess: () -> Unit // Callback to navigate after successful sign-up
) {
    // State for input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val viewModel: SignUpViewModel = viewModel(factory = PlotPotViewModelFactory(supabase))

    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Gradient background
    val gradientColors = listOf(
        Color(0xFF6B48FF), // Purple
        Color(0xFF00C4B4)  // Teal
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = gradientColors)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sign Up", color = Color.White, fontSize = 24.sp)
            // Logo (replace with your actual logo resource)
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.plotpotlogo), // Replace with your logo resource
                    contentDescription = "Logo",
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Email TextField
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password TextField
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.Black) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username TextField
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = Color.Black) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Button
            Button(
                onClick = {
                    viewModel.signUpUser(email, password, username)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B48FF), // Teal
                    contentColor = Color.Black
                ),
                enabled = uiState !is UiState.Loading
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 16.sp
                )
            }

            // Display error message if any
            when (uiState) {
                is UiState.Error -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = (uiState as UiState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                is UiState.Success -> {
                    // Navigate to the next screen on success
                    LaunchedEffect(Unit) {
                        onSignUpSuccess()
                    }
                }

                else -> {}
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Divider with "or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.White
                )
                Text(
                    text = "or",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Alternative Sign-Up Buttons (e.g., Google, Apple)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { /* Handle Google sign-up */ },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Text(
                        text = "G", // Placeholder for Google icon
                        color = Color.Black,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { /* Handle Apple sign-up */ },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Text(
                        text = "A", // Placeholder for Apple icon
                        color = Color.Black,
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(
        modifier = Modifier,
        onSignUpSuccess = {})
}