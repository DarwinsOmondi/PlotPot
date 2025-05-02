package com.example.plotpot.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.plotpot.customs.PlotPotViewModelFactory
import com.example.plotpot.customs.supabase
import com.example.plotpot.models.Challenge
import com.example.plotpot.models.Contribution
import com.example.plotpot.models.Story
import com.example.plotpot.models.UiState
import com.example.plotpot.utils.BottomNavBar
import com.example.plotpot.viewmodels.ChallengeViewModel
import com.example.plotpot.viewmodels.ContributionViewModel
import com.example.plotpot.viewmodels.StoryViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.jan.supabase.auth.auth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController) {
    // Initialize ViewModels using the factory
    val storyViewModel: StoryViewModel = viewModel(factory = PlotPotViewModelFactory(supabase))
    val challengeViewModel: ChallengeViewModel =
        viewModel(factory = PlotPotViewModelFactory(supabase))
    val contributionViewModel: ContributionViewModel =
        viewModel(factory = PlotPotViewModelFactory(supabase))

    // Fetch data
    storyViewModel.fetchStories(isCompleted = false) // Fetch incomplete stories for Featured Stories
    challengeViewModel.fetchChallenges() // Fetch active challenges

    // Collect UI states from ViewModels
    val storyState = storyViewModel.uiState.collectAsState().value
    val challengeState = challengeViewModel.uiState.collectAsState().value
    val contributionState = contributionViewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {

    }
    // Gradient background
    val gradientColors = listOf(
        Color(0xFF6B48FF), // Purple
        Color(0xFF00C4B4)  // Teal
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Create Story")
                }
            )
        },
        bottomBar = {
            BottomNavBar(navHostController)
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(
                        brush = Brush.verticalGradient(colors = gradientColors)
                    )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Featured Stories Section
                    item {
                        Text(
                            text = "Featured Stories",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        when (storyState) {
                            is UiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    color = Color.White
                                )
                            }

                            is UiState.Success -> {
                                if (storyState.data.stories.isEmpty()) {
                                    Text(
                                        text = "No featured stories available.",
                                        color = Color.White,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(storyState.data.stories) { story ->
                                            FeaturedStoriesCard(
                                                story = story,
                                                onClick = {
                                                    // Navigate to story details screen
                                                    navHostController.navigate("storyDetails/${story.id}")
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            is UiState.Error -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = storyState.message,
                                        color = Color.Red,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { storyViewModel.fetchStories(isCompleted = false) }) {
                                        Text(
                                            text = "Retry",
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            else -> {}
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Ongoing Challenges Section
                    item {
                        Text(
                            text = "Ongoing Challenges",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        when (challengeState) {
                            is UiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    color = Color.White
                                )
                            }

                            is UiState.Success -> {
                                if (challengeState.data.challenges.isEmpty()) {
                                    Text(
                                        text = "No ongoing challenges available.",
                                        color = Color.White,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(challengeState.data.challenges) { challenge ->
                                            ChallengeCard(
                                                challenge = challenge,
                                                onClick = {
                                                    // Navigate to challenge details screen
                                                    navHostController.navigate("challengeDetails/${challenge.id}")
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            is UiState.Error -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = challengeState.message,
                                        color = Color.Red,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { challengeViewModel.fetchChallenges() }) {
                                        Text(
                                            text = "Retry",
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            else -> {}
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Recent Contributions Section
                    item {
                        Text(
                            text = "Recent Contributions",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        when (contributionState) {
                            is UiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    color = Color.White
                                )
                            }

                            is UiState.Success -> {
                                if (contributionState.data.contributions.isEmpty()) {
                                    Text(
                                        text = "No recent contributions available.",
                                        color = Color.White,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else {
//                            LazyColumn(
//                                verticalArrangement = Arrangement.spacedBy(16.dp)
//                            ) {
//                                items(contributionState.data.contributions) { contribution ->
//                                    val story = storyViewModel.fetchStoryById(contribution.id)
//                                    ContributionCard(
//                                        contribution = contribution,
//                                        story = story,
//                                        onClick = {
//                                            // Navigate to the associated story
//                                            navHostController.navigate("storyDetails/${contribution.id}")
//                                        }
//                                    )
//                                }
//                            }
                                }
                            }

                            is UiState.Error -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = contributionState.message,
                                        color = Color.Red,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { contributionViewModel.fetchRecentContributions() }) {
                                        Text(
                                            text = "Retry",
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            else -> {}
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun FeaturedStoriesCard(story: Story, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = story.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = story.description ?: "No description available",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sentences: ${story.totalSentences}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ChallengeCard(challenge: Challenge, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = challenge.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = challenge.description ?: "No description available",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ends: ${challenge.endDate}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ContributionCard(contribution: Contribution, story: Story, onClick: () -> Unit) {
    val userName = supabase.auth.currentUserOrNull()?.userMetadata?.get("userName") ?: ""
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Contribution by $userName",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Story: ${story.description}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = contribution.sentence.take(100) + if (contribution.sentence.length > 100) "..." else "",
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Posted: ${contribution.createdAt}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}