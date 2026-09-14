package com.lingoleap.presentation.feature.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.Lesson
import com.lingoleap.domain.model.VocabularyWord

data class LessonState(val lesson: Lesson? = null, val currentWord: VocabularyWord? = null, val isAudioPlaying: Boolean = false) : UiState
sealed interface LessonEvent : UiEvent { data object Next : LessonEvent; data object Previous : LessonEvent; data object PlayAudio : LessonEvent; data object Finish : LessonEvent }

@Composable
fun LessonScreen(state: LessonState = LessonState(), onEvent: (LessonEvent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VocabularyCard(state.currentWord, onEvent)
        Button(modifier = Modifier.fillMaxWidth(), onClick = { onEvent(LessonEvent.Finish) }) { Text("Start quiz") }
    }
}

@Composable
fun VocabularyCard(word: VocabularyWord?, onEvent: (LessonEvent) -> Unit) {
    Text(word?.targetText ?: "Vocabulary lesson", style = MaterialTheme.typography.headlineMedium)
}
