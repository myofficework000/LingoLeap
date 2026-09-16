package com.lingoleap.presentation.feature.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.core.mvi.UiEffect
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.audio.PronunciationPlayer
import com.lingoleap.domain.model.Lesson
import com.lingoleap.domain.model.VocabularyWord
import com.lingoleap.domain.usecase.CompleteLessonUseCase
import com.lingoleap.domain.usecase.GetCoursesUseCase
import com.lingoleap.domain.usecase.LessonRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class LessonState(
    val lesson: Lesson? = null,
    val wordIndex: Int = 0,
    val viewedWordIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val isCompleting: Boolean = false,
    val error: String? = null,
    val audioError: String? = null
) : UiState {

    val currentWord: VocabularyWord?
        get() = lesson?.words?.getOrNull(wordIndex)
    val totalWords: Int
        get() = lesson?.words?.size ?: 0

    val isFirstWord: Boolean
        get() = wordIndex == 0

    val isLastWord: Boolean
        get() = totalWords > 0 && wordIndex == totalWords - 1

    val progress: Float
        get() = if (totalWords == 0) { 0f }
            else {
                (wordIndex + 1).toFloat() / totalWords
            }

    val canComplete: Boolean
        get() = lesson != null && LessonRules.canComplete(lesson = lesson, viewedWordIds = viewedWordIds)
}
sealed interface LessonEvent : UiEvent {
    data object Previous : LessonEvent
    data object Next : LessonEvent
    data object PlayAudio : LessonEvent
    data object Finish : LessonEvent
}

sealed interface LessonEffect : UiEffect {
    data class Completed(val lessonId: String) : LessonEffect
    data class ShowMessage(val message: String) : LessonEffect
}
@HiltViewModel
class LessonViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val completeLessonUseCase: CompleteLessonUseCase,
    private val pronunciationPlayer: PronunciationPlayer
) : ViewModel() {

    private val _state = MutableStateFlow(LessonState())
    val state = _state.asStateFlow()
    private val _effect = Channel<LessonEffect>(Channel.BUFFERED)

    val effect = _effect.receiveAsFlow()

    fun load(lessonId: String) {

        viewModelScope.launch {

            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            runCatching {

                getCoursesUseCase().flatMap { it.lessons }
                    .first { it.id == lessonId }

            }.onSuccess { lesson ->

                val firstWord = lesson.words.firstOrNull()

                _state.update {

                    LessonState(
                        lesson = lesson,
                        wordIndex = 0,
                        viewedWordIds = firstWord?.let { setOf(it.id) } ?: emptySet(),
                        isLoading = false
                    )
                }

            }.onFailure { exception ->

                _state.update {

                    LessonState(
                        isLoading = false,
                        error = exception.message ?: "Lesson unavailable"
                    )
                }
            }
        }
    }

    fun onEvent(
        event: LessonEvent
    ) {

        when (event) {

            LessonEvent.Previous -> {
                goToPreviousWord()
            }

            LessonEvent.Next -> {
                goToNextWord()
            }

            LessonEvent.PlayAudio -> {
                playAudio()
            }

            LessonEvent.Finish -> {
                finishLesson()
            }
        }
    }

    private fun goToPreviousWord() {

        _state.update { state ->

            state.copy(
                wordIndex = LessonRules.previousWordIndex(state.wordIndex)
            )
        }
    }

    private fun goToNextWord() {

        _state.update { state ->

            val lesson = state.lesson ?: return@update state

            val nextIndex =
                LessonRules.nextWordIndex(
                    currentIndex = state.wordIndex,
                    wordCount = lesson.words.size
                )

            val nextWord = lesson.words.getOrNull(nextIndex)

            state.copy(
                wordIndex = nextIndex,
                viewedWordIds =
                    if (nextWord != null) {
                        state.viewedWordIds + nextWord.id
                    } else {
                        state.viewedWordIds
                    }
            )
        }
    }

    private fun playAudio() {
            viewModelScope.launch {
                val state = _state.value

                val word = state.currentWord ?: return@launch

                val lesson = state.lesson ?: return@launch
                runCatching {
                    pronunciationPlayer.play(
                        text = word.targetText,
                        languageTag = lesson.pronunciationLanguageTag
                    )
                }.onSuccess { success ->
                    _state.update {
                        it.copy(
                            audioError = if (success) {
                                null
                            } else {
                                "Audio is unavailable on this device."
                            }
                        )
                    }
                }.onFailure { exception ->
                    println("LingoLeap TTS error = ${exception.message}")

                    _state.update {
                        it.copy(
                            audioError = exception.message
                                ?: "Unable to play pronunciation."
                        )
                    }
                }
            }
    }

    private fun finishLesson() {

        viewModelScope.launch {

            val state = _state.value
            val lesson = state.lesson ?: return@launch
            if (!state.canComplete) {
                _effect.send(LessonEffect.ShowMessage("Please view every word before completing the lesson.")
                )

                return@launch
            }
            _state.update {
                it.copy(isCompleting = true)
            }

            runCatching {
                completeLessonUseCase(lesson.id)

            }.onSuccess {
                _state.update { it.copy(isCompleting = false) }
                _effect.send(LessonEffect.Completed(lesson.id)
                )

            }.onFailure { exception ->

                _state.update {
                    it.copy(
                        isCompleting = false,
                        error = exception.message ?: "Unable to complete lesson"
                    )
                }
            }
        }
    }

    override fun onCleared() {

        pronunciationPlayer.release()

        super.onCleared()
    }
}