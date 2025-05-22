package com.example.plotpot.screens.create

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.plotpot.customs.PlotPotViewModelFactory
import com.example.plotpot.customs.supabase
import com.example.plotpot.utils.BottomNavBar
import com.example.plotpot.viewmodels.StoryViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoryScreen(navHostController: NavHostController, context: Context) {
    val story = remember { mutableStateOf("") }
    val storyViewModel: StoryViewModel = viewModel(
        factory = PlotPotViewModelFactory(supabase)
    )

    // Gradient background
    val gradientColors = listOf(
        Color(0xFF6B48FF), // Purple
        Color(0xFF00C4B4)  // Teal
    )
    val lines = story.value.lines()
    val title = lines.take(1).joinToString("\n") // First 3 lines
    val body = if (lines.size > 1) lines.drop(1).joinToString("\n") else ""
    val switchChecked: Boolean = false
    val onSwitchChange: ((Boolean) -> Unit)? = null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create Story",
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondary),
            )
        },

        bottomBar = {
            BottomNavBar(navHostController)
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(colors = gradientColors)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    OutlinedTextField(
                        value = story.value,
                        onValueChange = { story.value = it },
                        placeholder = {
                            Text(
                                "Create your Story(First line is for TITLE)",
                                style = TextStyle(color = Color.Black)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                            focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onTertiary,
                        ),
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    try {
                        storyViewModel.createStory(
                            title = title,
                            description = body,
                            totalSentences = lines.size
                        )
                        Toast.makeText(context, "Story Created Successfully", Toast.LENGTH_LONG)
                            .show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error Creating Story", Toast.LENGTH_LONG).show()
                    }
                },
                content = {
                    Icon(
                        Icons.Default.PostAdd,
                        contentDescription = "Add story"
                    )
                },
                contentColor = MaterialTheme.colorScheme.onTertiary,
                containerColor = MaterialTheme.colorScheme.secondary,
            )
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CreateStoryScreenPreview() {
    CreateStoryScreen(
        navHostController = NavHostController(LocalContext.current),
        LocalContext.current
    )
}