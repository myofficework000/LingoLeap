package com.lingoleap.presentation.feature.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.usecase.GetLessonQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getLessonQuizUseCase:
    GetLessonQuizUseCase
) : ViewModel() {

    private val _quizState =
        MutableStateFlow(
            QuizState()
        )

    val quizState: StateFlow<QuizState> =
        _quizState.asStateFlow()

    fun loadQuiz(
        lessonId: String
    ) {

        viewModelScope.launch {

            val quizzes =
                getLessonQuizUseCase(
                    lessonId
                )

            _quizState.update {
                it.copy(
                    quizzes = quizzes,
                    currentQuestionIndex = 0,
                    selectedAnswer = null,
                    isAnswerChecked = false,
                    score = 0,
                    isQuizCompleted = false,
                    answerResults =
                        emptyList(),
                    showMistakes = false
                )
            }
        }
    }

    fun onEvent(
        event: QuizEvent
    ) {

        when (event) {

            is QuizEvent.SelectAnswer -> {
                selectAnswer(
                    event.answer
                )
            }

            QuizEvent.CheckAnswer -> {
                checkAnswer()
            }

            QuizEvent.Next -> {
                moveToNextQuestion()
            }

            QuizEvent.ReviewMistakes -> {

                _quizState.update {
                    it.copy(
                        showMistakes = true
                    )
                }
            }

            QuizEvent.BackToResults -> {

                _quizState.update {
                    it.copy(
                        showMistakes = false
                    )
                }
            }

            QuizEvent.RetryQuiz -> {
                retryQuiz()
            }
        }
    }

    private fun selectAnswer(
        answer: String
    ) {

        if (_quizState.value.isAnswerChecked) {
            return
        }

        _quizState.update {
            it.copy(
                selectedAnswer = answer
            )
        }
    }

    private fun checkAnswer() {

        val state =
            _quizState.value

        if (state.isAnswerChecked) {
            return
        }

        val currentQuiz =
            state.quizzes.getOrNull(
                state.currentQuestionIndex
            ) ?: return

        val selectedAnswer =
            state.selectedAnswer
                ?: return

        val isCorrect =
            selectedAnswer ==
                    currentQuiz.correctAnswer

        val result =
            QuizAnswerResult(
                quiz = currentQuiz,
                selectedAnswer =
                    selectedAnswer,
                isCorrect = isCorrect
            )

        _quizState.update {

            it.copy(
                isAnswerChecked = true,

                score =
                    if (isCorrect) {
                        it.score + 1
                    } else {
                        it.score
                    },

                answerResults =
                    it.answerResults +
                            result
            )
        }
    }

    private fun moveToNextQuestion() {

        val state =
            _quizState.value

        if (!state.isAnswerChecked) {
            return
        }

        val isLastQuestion =
            state.currentQuestionIndex ==
                    state.quizzes.lastIndex

        if (isLastQuestion) {

            _quizState.update {
                it.copy(
                    isQuizCompleted = true,
                    showMistakes = false
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

    private fun retryQuiz() {

        _quizState.update {
            it.copy(
                currentQuestionIndex = 0,
                selectedAnswer = null,
                isAnswerChecked = false,
                score = 0,
                isQuizCompleted = false,
                answerResults = emptyList(),
                showMistakes = false
            )
        }
    }
}