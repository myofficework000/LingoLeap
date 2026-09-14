package com.lingoleap.presentation.feature.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.Quiz

data class QuizState(
    val quiz: Quiz? = null,
    val selectedAnswer: String? = null,
    val isAnswerChecked: Boolean = false
) : UiState

sealed interface QuizEvent : UiEvent {
    data class SelectAnswer(val answer: String) : QuizEvent;
    data object CheckAnswer : QuizEvent;
    data object Next : QuizEvent
}

@Composable
fun QuizRoute(
    lessonId: String,
    onNext: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
){
    val state by viewModel.quizState.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) {
        viewModel.loadQuiz(lessonId)
    }

    QuizScreen(
        state = state,
        onEvent = { event -> if (event == QuizEvent.Next) onNext() else viewModel.onEvent(event) },
    )
}

@Composable
fun QuizScreen(state: QuizState = QuizState(), onEvent: (QuizEvent) -> Unit) {

    val quiz = state.quiz
    val colors = MaterialTheme.colorScheme

    if (quiz == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column() {
        Text(
            text = quiz.prompt,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        quiz.choices.forEach { answer ->

            val isSelected = state.selectedAnswer == answer
            val isCorrectAnswer = answer == quiz.correctAnswer

            val containerColor = when {
                state.isAnswerChecked && isCorrectAnswer ->
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
                        QuizEvent.SelectAnswer(answer)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = containerColor
                ),
                enabled = !state.isAnswerChecked
            ) {
                Text(answer)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (!state.isAnswerChecked) {
                    onEvent(QuizEvent.CheckAnswer)
                } else {
                    onEvent(QuizEvent.Next)
                }
            },
            enabled = state.selectedAnswer != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (state.isAnswerChecked) {
                    "Next"
                } else {
                    "Check Answer"
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {

    val fakeQuiz = Quiz(
        id = "quiz-1",
        lessonId = "hi-basics",
        prompt = "Choose the correct meaning",
        choices = listOf(
            "Book",
            "Pen",
            "Table",
            "Chair"
        ),
        correctAnswer = "Book"
    )

    QuizScreen(
        state = QuizState(
            quiz = fakeQuiz,
            selectedAnswer = "Book",
            isAnswerChecked = false
        ),
        onEvent = {}
    )
}
