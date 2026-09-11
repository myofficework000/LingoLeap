package com.lingoleap.presentation.feature.profile

import androidx.compose.runtime.Composable
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState

data class ProfileState(val displayName: String = "Learner", val selectedCourseCount: Int = 0) : UiState
sealed interface ProfileEvent : UiEvent { data object OpenLanguages : ProfileEvent; data object OpenSettings : ProfileEvent; data object SignOut : ProfileEvent }

@Composable fun ProfileScreen(state: ProfileState = ProfileState(), onEvent: (ProfileEvent) -> Unit) { ProfileHeader(state.displayName, onEvent) }
@Composable fun ProfileHeader(displayName: String, onEvent: (ProfileEvent) -> Unit) = Unit
