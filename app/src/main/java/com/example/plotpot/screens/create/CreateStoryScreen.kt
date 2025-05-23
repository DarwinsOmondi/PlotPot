package com.example.plotpot.screens.create

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.plotpot.customs.PlotPotViewModelFactory
import com.example.plotpot.customs.supabase
import com.example.plotpot.models.UiState
import com.example.plotpot.ui.theme.*
import com.example.plotpot.utils.BottomNavBar
import com.example.plotpot.viewmodels.StoryViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoryScreen(
    navHostController: NavHostController,
    context: Context = LocalContext.current
) {
    val storyViewModel: StoryViewModel = viewModel(factory = PlotPotViewModelFactory(supabase))
    var storyTitle by remember { mutableStateOf("") }
    var storyBody by remember { mutableStateOf("") }
    val lines = storyBody.lines().filter { it.isNotBlank() }
    val createState by storyViewModel.createUiState.collectAsState()

    // Handle creation state
    LaunchedEffect(createState) {
        when (val state = createState) {
            is UiState.Success -> {
                Toast.makeText(context, "Story Created Successfully", Toast.LENGTH_LONG).show()
                navHostController.popBackStack() // Navigate back to HomeScreen
            }

            is UiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    PlotPotTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Create Story",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                BottomNavBar(navHostController)
            },
            floatingActionButton = {
                GradientFAB(
                    onClick = {
                        storyViewModel.createStory(
                            title = storyTitle,
                            description = storyBody,
                            totalSentences = lines.size
                        )
                    },
                    isLoading = createState is UiState.Loading
                )
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = storyTitle,
                            onValueChange = { storyTitle = it },
                            placeholder = {
                                Text(
                                    text = "Title",
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 16.sp
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.3f
                                ),
                                cursorColor = MaterialTheme.colorScheme.secondary
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences
                            ),
                            maxLines = 2,
                            singleLine = false
                        )
                        OutlinedTextField(
                            value = storyBody,
                            onValueChange = { storyBody = it },
                            placeholder = {
                                Text(
                                    text = "Write your story here...",
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 16.sp
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp)),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.3f
                                ),
                                cursorColor = MaterialTheme.colorScheme.secondary
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences
                            ),
                            singleLine = false
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun GradientFAB(onClick: () -> Unit, isLoading: Boolean) {
    val scale = remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale.value,
        animationSpec = tween(durationMillis = 100)
    )

    FloatingActionButton(
        onClick = {
            if (!isLoading) {
                scale.value = 0.95f
                onClick()
                scale.value = 1f
            }
        },
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .scale(animatedScale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        content = {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    Icons.Default.PostAdd,
                    contentDescription = "Create story",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CreateStoryScreenPreview() {
    PlotPotTheme {
        CreateStoryScreen(
            navHostController = NavHostController(LocalContext.current),
            context = LocalContext.current
        )
    }
}