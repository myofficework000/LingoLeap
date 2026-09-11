package com.lingoleap.presentation.feature.quiz

import androidx.compose.runtime.Composable
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.Quiz

data class QuizState(val quiz: Quiz? = null, val selectedAnswer: String? = null, val isAnswerChecked: Boolean = false) : UiState
sealed interface QuizEvent : UiEvent { data class SelectAnswer(val answer: String) : QuizEvent; data object CheckAnswer : QuizEvent; data object Next : QuizEvent }

@Composable fun QuizScreen(state: QuizState = QuizState(), onEvent: (QuizEvent) -> Unit) { MultipleChoiceQuestion(state.quiz, state.selectedAnswer, onEvent) }
@Composable fun MultipleChoiceQuestion(quiz: Quiz?, selectedAnswer: String?, onEvent: (QuizEvent) -> Unit) = Unit
