package com.lingoleap.presentation.feature.home

import androidx.compose.runtime.Composable
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.LearnerProgress

data class HomeState(val course: Course? = null, val progress: LearnerProgress? = null) : UiState
sealed interface HomeEvent : UiEvent { data object ContinueLearning : HomeEvent; data object OpenPractice : HomeEvent; data object OpenProfile : HomeEvent }

@Composable fun HomeScreen(state: HomeState = HomeState(), onEvent: (HomeEvent) -> Unit) { ContinueLearningCard(state.course, onEvent) }
@Composable fun ContinueLearningCard(course: Course?, onEvent: (HomeEvent) -> Unit) = Unit
