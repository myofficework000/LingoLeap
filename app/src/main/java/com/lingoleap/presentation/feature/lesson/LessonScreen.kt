package com.lingoleap.presentation.feature.lesson

import androidx.compose.runtime.Composable
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.Lesson
import com.lingoleap.domain.model.VocabularyWord

data class LessonState(val lesson: Lesson? = null, val currentWord: VocabularyWord? = null, val isAudioPlaying: Boolean = false) : UiState
sealed interface LessonEvent : UiEvent { data object Next : LessonEvent; data object Previous : LessonEvent; data object PlayAudio : LessonEvent; data object Finish : LessonEvent }

@Composable fun LessonScreen(state: LessonState = LessonState(), onEvent: (LessonEvent) -> Unit) { VocabularyCard(state.currentWord, onEvent) }
@Composable fun VocabularyCard(word: VocabularyWord?, onEvent: (LessonEvent) -> Unit) = Unit
