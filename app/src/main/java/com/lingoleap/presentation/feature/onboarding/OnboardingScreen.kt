package com.lingoleap.presentation.feature.onboarding

import androidx.compose.runtime.Composable
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState

data class OnboardingState(val pageIndex: Int = 0, val isLoading: Boolean = false) : UiState
sealed interface OnboardingEvent : UiEvent { data object Continue : OnboardingEvent; data object Skip : OnboardingEvent }

@Composable fun OnboardingScreen(state: OnboardingState = OnboardingState(), onEvent: (OnboardingEvent) -> Unit) { OnboardingPage(state.pageIndex, onEvent) }
@Composable fun OnboardingPage(pageIndex: Int, onEvent: (OnboardingEvent) -> Unit) = Unit
