package com.example.plotpot.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plotpot.customs.PlotPotViewModelFactory
import com.example.plotpot.customs.supabase
import com.example.plotpot.viewmodels.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoryScreen() {
    val story = remember { mutableStateOf("") }
    val storyViewModel: StoryViewModel = viewModel(
        factory = PlotPotViewModelFactory(supabase)
    )
    val gradientColors = listOf(
        Color(0xFF6B48FF), // Purple
        Color(0xFF00C4B4)  // Teal
    )

    val lines = story.value.lines()
    val title = lines.take(3).joinToString("\n") // First 3 lines
    val body = if (lines.size > 3) lines.drop(3).joinToString("\n") else ""


    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("Create Story")
            }
        )
    }) { innerPadding ->
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
                    .padding(16.dp)
            ) {
                Text("Write your Story")

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = story.value,
                    onValueChange = { story.value = it },
                    placeholder = { Text("Enter story description") },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {},
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Save Story")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreateStoryScreenPreview() {
    CreateStoryScreen(
    )
}