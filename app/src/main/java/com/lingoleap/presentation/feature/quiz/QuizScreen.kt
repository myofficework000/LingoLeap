package com.lingoleap.presentation.feature.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.Quiz

data class QuizAnswerResult(
    val quiz: Quiz,
    val selectedAnswer: String,
    val isCorrect: Boolean
)

data class QuizState(
    val quizzes: List<Quiz> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerChecked: Boolean = false,
    val score: Int = 0,
    val isQuizCompleted: Boolean = false,
    val answerResults: List<QuizAnswerResult> = emptyList(),
    val showMistakes: Boolean = false
) : UiState

sealed interface QuizEvent : UiEvent {

    data class SelectAnswer(
        val answer: String
    ) : QuizEvent

    data object CheckAnswer : QuizEvent

    data object Next : QuizEvent

    data object ReviewMistakes : QuizEvent

    data object BackToResults : QuizEvent

    data object RetryQuiz : QuizEvent
}

@Composable
fun QuizRoute(
    lessonId: String,
    onNext: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {

    val state by
    viewModel.quizState.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) {
        viewModel.loadQuiz(lessonId)
    }

    when {

        state.isQuizCompleted &&
                state.showMistakes -> {

            QuizMistakesScreen(
                mistakes =
                    state.answerResults.filter {
                        !it.isCorrect
                    },
                onBack = {
                    viewModel.onEvent(
                        QuizEvent.BackToResults
                    )
                }
            )
        }

        state.isQuizCompleted -> {

            QuizCompletedScreen(
                score = state.score,
                total = state.quizzes.size,
                wrongCount =
                    state.answerResults.count {
                        !it.isCorrect
                    },
                onReviewMistakes = {
                    viewModel.onEvent(
                        QuizEvent.ReviewMistakes
                    )
                },
                onRetry = {
                    viewModel.onEvent(
                        QuizEvent.RetryQuiz
                    )
                },
                onContinue = onNext
            )
        }

        else -> {

            QuizScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@Composable
fun QuizScreen(
    state: QuizState = QuizState(),
    onEvent: (QuizEvent) -> Unit
) {

    val quiz =
        state.quizzes.getOrNull(
            state.currentQuestionIndex
        )

    val colors =
        MaterialTheme.colorScheme

    if (quiz == null) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text =
                "Question ${state.currentQuestionIndex + 1} " +
                        "of ${state.quizzes.size}",
            style =
                MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = quiz.prompt,
            style =
                MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        quiz.choices.forEach { answer ->

            val isSelected =
                state.selectedAnswer == answer

            val isCorrectAnswer =
                answer == quiz.correctAnswer

            val containerColor =
                when {

                    state.isAnswerChecked &&
                            isCorrectAnswer ->
                        colors.primaryContainer

                    state.isAnswerChecked &&
                            isSelected ->
                        colors.errorContainer

                    isSelected ->
                        colors.secondaryContainer

                    else ->
                        colors.surface
                }

            OutlinedButton(
                onClick = {
                    onEvent(
                        QuizEvent.SelectAnswer(
                            answer
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor =
                            containerColor
                    ),
                enabled =
                    !state.isAnswerChecked
            ) {

                Text(answer)
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {

                if (!state.isAnswerChecked) {

                    onEvent(
                        QuizEvent.CheckAnswer
                    )

                } else {

                    onEvent(
                        QuizEvent.Next
                    )
                }
            },
            enabled =
                state.selectedAnswer != null,
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                when {

                    !state.isAnswerChecked ->
                        "Check Answer"

                    state.currentQuestionIndex ==
                            state.quizzes.lastIndex ->
                        "Finish"

                    else ->
                        "Next"
                }
            )
        }
    }
}

@Composable
fun QuizCompletedScreen(
    score: Int,
    total: Int,
    wrongCount: Int,
    onReviewMistakes: () -> Unit,
    onRetry: () -> Unit,
    onContinue: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Quiz Complete!",
            style =
                MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Score: $score / $total",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Correct: $score"
        )

        Text(
            text = "Wrong: $wrongCount"
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        if (wrongCount > 0) {

            OutlinedButton(
                onClick = onReviewMistakes,
                modifier =
                    Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Review Mistakes"
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        OutlinedButton(
            onClick = onRetry,
            modifier =
                Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Retry Quiz"
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onContinue,
            modifier =
                Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Continue to Practice"
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuizMistakesScreen(
    mistakes: List<QuizAnswerResult>,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Review Mistakes",
            style =
                MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(mistakes) { result ->

                Text(
                    text = result.quiz.prompt,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Your answer: ${result.selectedAnswer}",
                    color =
                        MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        "Correct answer: ${result.quiz.correctAnswer}",
                    color =
                        MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }
        }

        Button(
            onClick = onBack,
            modifier =
                Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Back to Results"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {

    val fakeQuiz =
        Quiz(
            id = "quiz-1",
            lessonId = "hi-basics",
            prompt =
                "Choose the correct meaning",
            choices =
                listOf(
                    "Book",
                    "Pen",
                    "Table",
                    "Chair"
                ),
            correctAnswer = "Book"
        )

    QuizScreen(
        state =
            QuizState(
                quizzes =
                    listOf(fakeQuiz),
                currentQuestionIndex = 0,
                selectedAnswer = "Book",
                isAnswerChecked = false
            ),
        onEvent = {}
    )
}