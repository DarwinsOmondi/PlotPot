package com.example.plotpot.customs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plotpot.viewmodels.AnimationViewModel
import com.example.plotpot.viewmodels.ChallengeViewModel
import com.example.plotpot.viewmodels.ContributionViewModel
import com.example.plotpot.viewmodels.ProfileViewModel
import com.example.plotpot.viewmodels.StoryViewModel
import com.example.plotpot.viewmodels.VoteViewModel
import io.github.jan.supabase.SupabaseClient

class PlotPotViewModelFactory(private val supabase: SupabaseClient) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(supabase) as T
            }

            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(supabase) as T
            }

            modelClass.isAssignableFrom(ContributionViewModel::class.java) -> {
                ContributionViewModel(supabase) as T
            }

            modelClass.isAssignableFrom(VoteViewModel::class.java) -> {
                VoteViewModel(supabase) as T
            }

            modelClass.isAssignableFrom(AnimationViewModel::class.java) -> {
                AnimationViewModel(supabase) as T
            }

            modelClass.isAssignableFrom(ChallengeViewModel::class.java) -> {
                ChallengeViewModel(supabase) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}