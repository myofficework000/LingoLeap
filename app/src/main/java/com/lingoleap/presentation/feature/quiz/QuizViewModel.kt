package com.lingoleap.presentation.feature.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.usecase.GetLessonQuizUseCase
import com.lingoleap.domain.usecase.AddReviewWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getLessonQuizUseCase: GetLessonQuizUseCase,
    private val addReviewWord: AddReviewWordUseCase,
) : ViewModel() {

    private val _quizState =
        MutableStateFlow(QuizState())

    val quizState: StateFlow<QuizState> =
        _quizState.asStateFlow()

    fun loadQuiz(lessonId: String) {
        viewModelScope.launch {

            val quizzes =
                getLessonQuizUseCase(lessonId)

            _quizState.update {
                it.copy(
                    quizzes = quizzes,
                    currentQuestionIndex = 0,
                    selectedAnswer = null,
                    isAnswerChecked = false,
                    score = 0,
                    isQuizCompleted = false
                )
            }
        }
    }

    fun onEvent(event: QuizEvent) {

        when (event) {

            is QuizEvent.SelectAnswer -> {
                if (!_quizState.value.isAnswerChecked) {
                    _quizState.update {
                        it.copy(
                            selectedAnswer = event.answer
                        )
                    }
                }
            }

            QuizEvent.CheckAnswer -> {
                checkAnswer()
            }

            QuizEvent.Next -> {
                moveToNextQuestion()
            }
        }
    }

    private fun checkAnswer() {

        val state = _quizState.value

        if (state.isAnswerChecked) {
            return
        }

        val currentQuiz =
            state.quizzes.getOrNull(
                state.currentQuestionIndex
            ) ?: return

        val selectedAnswer =
            state.selectedAnswer ?: return

        val isCorrect =
            selectedAnswer ==
                    currentQuiz.correctAnswer

        _quizState.update {
            it.copy(
                isAnswerChecked = true,
                score =
                    if (isCorrect) {
                        it.score + 1
                    } else {
                        it.score
                    }
            )
        }
        if (!isCorrect) {
            viewModelScope.launch { addReviewWord("${currentQuiz.lessonId}-${state.currentQuestionIndex + 1}") }
        }
    }

    private fun moveToNextQuestion() {

        val state = _quizState.value

        if (!state.isAnswerChecked) {
            return
        }

        val isLastQuestion =
            state.currentQuestionIndex ==
                    state.quizzes.lastIndex

        if (isLastQuestion) {

            _quizState.update {
                it.copy(
                    isQuizCompleted = true
                )
            }

        } else {

            _quizState.update {
                it.copy(
                    currentQuestionIndex =
                        it.currentQuestionIndex + 1,
                    selectedAnswer = null,
                    isAnswerChecked = false
                )
            }
        }
    }
}
