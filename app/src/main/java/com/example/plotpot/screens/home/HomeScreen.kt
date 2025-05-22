package com.example.plotpot.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.plotpot.ui.theme.*
import com.example.plotpot.utils.BottomNavBar
import com.example.plotpot.viewmodels.ChallengeViewModel
import com.example.plotpot.viewmodels.ContributionViewModel
import com.example.plotpot.viewmodels.StoryViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.jan.supabase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController) {
    val storyViewModel: StoryViewModel = viewModel(factory = PlotPotViewModelFactory(supabase))
    val challengeViewModel: ChallengeViewModel =
        viewModel(factory = PlotPotViewModelFactory(supabase))
    val contributionViewModel: ContributionViewModel =
        viewModel(factory = PlotPotViewModelFactory(supabase))

    // Fetch data
    LaunchedEffect(Unit) {
        storyViewModel.fetchStories(isCompleted = false)
        challengeViewModel.fetchChallenges()
        contributionViewModel.fetchRecentContributions()
    }

    // Collect UI states
    val storyState by storyViewModel.uiState.collectAsState()
    val challengeState by challengeViewModel.uiState.collectAsState()
    val contributionState by contributionViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Story",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
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
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Featured Stories Section
                    item {
                        SectionTitle("Featured Stories")
                        when (val state = storyState) { // Assign to local variable
                            is UiState.Loading -> LoadingIndicator()
                            is UiState.Success -> {
                                if (state.data.stories.isEmpty()) {
                                    EmptyStateMessage("No featured stories available.")
                                } else {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(state.data.stories) { story ->
                                            FeaturedStoriesCard(
                                                story = story,
                                                onClick = {
                                                    navHostController.navigate("storyDetails/${story.id}")
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            is UiState.Error -> ErrorState(
                                message = state.message,
                                onRetry = { storyViewModel.fetchStories(isCompleted = false) }
                            )

                            UiState.Initial -> TODO()
                        }
                    }

                    // Ongoing Challenges Section
                    item {
                        SectionTitle("Ongoing Challenges")
                        when (val state = challengeState) { // Assign to local variable
                            is UiState.Loading -> LoadingIndicator()
                            is UiState.Success -> {
                                if (state.data.challenges.isEmpty()) {
                                    EmptyStateMessage("No ongoing challenges available.")
                                } else {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(state.data.challenges) { challenge ->
                                            ChallengeCard(
                                                challenge = challenge,
                                                onClick = {
                                                    navHostController.navigate("challengeDetails/${challenge.id}")
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            is UiState.Error -> ErrorState(
                                message = state.message,
                                onRetry = { challengeViewModel.fetchChallenges() }
                            )

                            UiState.Initial -> TODO()
                        }
                    }

                    // Recent Contributions Section
//                    item {
//                        SectionTitle("Recent Contributions")
//                        when (val state = contributionState) { // Assign to local variable
//                            is UiState.Loading -> LoadingIndicator()
//                            is UiState.Success -> {
//                                if (state.data.contributions.isEmpty()) {
//                                    EmptyStateMessage("No recent contributions available.")
//                                } else {
//                                    LazyColumn(
//                                        verticalArrangement = Arrangement.spacedBy(12.dp)
//                                    ) {
//                                        items(state.data.contributions) { contribution ->
//                                            val storyStateForContribution by storyViewModel.fetchStoryById(
//                                                contribution.storyId
//                                            ).collectAsState()
//                                            when (val storyState = storyStateForContribution) {
//                                                is UiState.Success -> {
//                                                    ContributionCard(
//                                                        contribution = contribution,
//                                                        story = storyState.data,
//                                                        onClick = {
//                                                            navHostController.navigate("storyDetails/${contribution.storyId}")
//                                                        }
//                                                    )
//                                                }
//
//                                                else -> {} // Optionally handle loading/error for story
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            is UiState.Error -> ErrorState(
//                                message = state.message,
//                                onRetry = { contributionViewModel.fetchRecentContributions() }
//                            )
//
//                            UiState.Initial -> TODO()
//                        }
//                    }
                }
            }
        }
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun LoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun EmptyStateMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        fontSize = 16.sp,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        GradientButton(
            text = "Retry",
            onClick = onRetry
        )
    }
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit) {
    val scale = remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale.value,
        animationSpec = tween(durationMillis = 100)
    )

    Button(
        onClick = {
            scale.value = 0.95f
            onClick()
            scale.value = 1f
        },
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
            .scale(animatedScale),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FeaturedStoriesCard(story: Story, onClick: () -> Unit) {
    val scale = remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale.value,
        animationSpec = tween(durationMillis = 100)
    )

    Card(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                scale.value = 0.95f
                onClick()
                scale.value = 1f
            }
            .scale(animatedScale),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = story.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = story.description ?: "No description available",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sentences: ${story.totalSentences}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ChallengeCard(challenge: Challenge, onClick: () -> Unit) {
    val scale = remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale.value,
        animationSpec = tween(durationMillis = 100)
    )

    Card(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                scale.value = 0.95f
                onClick()
                scale.value = 1f
            }
            .scale(animatedScale),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = challenge.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = challenge.description ?: "No description available",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ends: ${challenge.endDate}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ContributionCard(contribution: Contribution, story: Story, onClick: () -> Unit) {
    val scale = remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale.value,
        animationSpec = tween(durationMillis = 100)
    )

    val userName =
        supabase.auth.currentUserOrNull()?.userMetadata?.get("userName")?.toString() ?: "Anonymous"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                scale.value = 0.95f
                onClick()
                scale.value = 1f
            }
            .scale(animatedScale),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Contribution by $userName",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Story: ${story.title}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = contribution.sentence.take(100) + if (contribution.sentence.length > 100) "..." else "",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Posted: ${contribution.createdAt}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}