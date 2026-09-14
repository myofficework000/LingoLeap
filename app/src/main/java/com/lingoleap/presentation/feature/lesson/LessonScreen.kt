package com.lingoleap.presentation.feature.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.lingoleap.core.mvi.UiEffect
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.audio.PronunciationPlayer
import com.lingoleap.domain.model.Lesson
import com.lingoleap.domain.model.VocabularyWord
import com.lingoleap.domain.usecase.CompleteLessonUseCase
import com.lingoleap.domain.usecase.GetCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonState(val lesson: Lesson? = null, val wordIndex: Int = 0, val isLoading: Boolean = true, val error: String? = null) : UiState {
    val currentWord: VocabularyWord? get() = lesson?.words?.getOrNull(wordIndex)
    val isLastWord: Boolean get() = lesson?.words?.lastIndex == wordIndex
}
sealed interface LessonEvent : UiEvent { data object Next : LessonEvent; data object Previous : LessonEvent; data object PlayAudio : LessonEvent; data object Finish : LessonEvent }
sealed interface LessonEffect : UiEffect { data object Completed : LessonEffect }

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val getCourses: GetCoursesUseCase,
    private val completeLesson: CompleteLessonUseCase,
    private val pronunciationPlayer: PronunciationPlayer,
) : ViewModel() {
    private val _state = MutableStateFlow(LessonState())
    val state = _state.asStateFlow()
    private val _effect = Channel<LessonEffect>(Channel.BUFFERED)
    val effect: Flow<LessonEffect> = _effect.receiveAsFlow()

    fun load(lessonId: String) = viewModelScope.launch {
        runCatching { getCourses().flatMap { it.lessons }.first { it.id == lessonId } }
            .onSuccess { lesson -> _state.update { LessonState(lesson = lesson, isLoading = false) } }
            .onFailure { error -> _state.update { LessonState(isLoading = false, error = error.message ?: "Lesson unavailable") } }
    }

    fun onEvent(event: LessonEvent) = when (event) {
        LessonEvent.Next -> _state.update { it.copy(wordIndex = (it.wordIndex + 1).coerceAtMost(it.lesson?.words?.lastIndex ?: 0)) }
        LessonEvent.Previous -> _state.update { it.copy(wordIndex = (it.wordIndex - 1).coerceAtLeast(0)) }
        LessonEvent.PlayAudio -> viewModelScope.launch { _state.value.currentWord?.let { pronunciationPlayer.play(it.targetText, "hi-IN") } }
        LessonEvent.Finish -> viewModelScope.launch { _state.value.lesson?.let { completeLesson(it.id); _effect.send(LessonEffect.Completed) } }
    }
    override fun onCleared() { pronunciationPlayer.release() }
}

@Composable
fun LessonRoute(lessonId: String, onCompleted: () -> Unit, viewModel: LessonViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(lessonId) { viewModel.load(lessonId) }
    LaunchedEffect(viewModel) { viewModel.effect.collect { if (it is LessonEffect.Completed) onCompleted() } }
    LessonScreen(state, viewModel::onEvent)
}

@Composable
fun LessonScreen(state: LessonState, onEvent: (LessonEvent) -> Unit) = Column(
    modifier = Modifier.fillMaxSize().padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    val word = state.currentWord
    Text(word?.targetText ?: state.error ?: "Loading lesson…", style = MaterialTheme.typography.headlineMedium)
    Text(word?.transliteration.orEmpty(), style = MaterialTheme.typography.titleMedium)
    OutlinedButton(onClick = { onEvent(LessonEvent.PlayAudio) }, enabled = word != null) { Text("Play pronunciation") }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        OutlinedButton(onClick = { onEvent(LessonEvent.Previous) }, enabled = state.wordIndex > 0) { Text("Previous") }
        Button(onClick = { onEvent(if (state.isLastWord) LessonEvent.Finish else LessonEvent.Next) }, enabled = word != null) { Text(if (state.isLastWord) "Start quiz" else "Next") }
    }
}
