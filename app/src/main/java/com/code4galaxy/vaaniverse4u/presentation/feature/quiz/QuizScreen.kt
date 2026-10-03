package com.code4galaxy.vaaniverse4u.presentation.feature.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.code4galaxy.vaaniverse4u.R
import com.code4galaxy.vaaniverse4u.core.mvi.UiEvent
import com.code4galaxy.vaaniverse4u.core.mvi.UiState
import com.code4galaxy.vaaniverse4u.domain.model.Quiz

data class QuizState(
    val quizzes: List<Quiz> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerChecked: Boolean = false,
    val score: Int = 0,
    val isQuizCompleted: Boolean = false,
) : UiState

sealed interface QuizEvent : UiEvent {
    data class SelectAnswer(val answer: String) : QuizEvent
    data object CheckAnswer : QuizEvent
    data object Next : QuizEvent
    data object PlayAudio : QuizEvent
}

@Composable
fun QuizRoute(lessonId: String, onNext: () -> Unit, viewModel: QuizViewModel = hiltViewModel()) {
    val state by viewModel.quizState.collectAsStateWithLifecycle()
    LaunchedEffect(lessonId) { viewModel.loadQuiz(lessonId) }
    if (state.isQuizCompleted) QuizCompletedScreen(state.score, state.quizzes.size, onNext)
    else QuizScreen(state, viewModel::onEvent)
}

@Composable
fun QuizScreen(state: QuizState = QuizState(), onEvent: (QuizEvent) -> Unit) {
    val quiz = state.quizzes.getOrNull(state.currentQuestionIndex)
    if (quiz == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val questionNumber = state.currentQuestionIndex + 1
    val progress = questionNumber.toFloat() / state.quizzes.size.coerceAtLeast(1)
    Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(0.dp))) {
        Image(
            painter = painterResource(R.drawable.quiz_celebration), contentDescription = null,
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, alignment = Alignment.BottomCenter,
        )
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = QuizNavy)
                Spacer(Modifier.width(8.dp))
                Text("Vocabulary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = QuizNavy)
                Spacer(Modifier.weight(1f))
                Text("+$questionNumber", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = QuizSaffron)
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { progress }, modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(99.dp)),
                    color = QuizGreen, trackColor = Color(0xFFE6E9E4),
                )
                Spacer(Modifier.width(12.dp))
                Text("$questionNumber / ${state.quizzes.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = QuizNavy)
            }
            Spacer(Modifier.height(22.dp))
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFDFFFFFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            ) {
                Column(modifier = Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(quiz.prompt, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = QuizNavy, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(18.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFFFF2CC)) {
                        Row(modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (quiz.prompt.startsWith("Listen")) "Tap the sound to listen" else quiz.pronunciationText,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = QuizNavy,
                                textAlign = TextAlign.Center,
                            )
                            IconButton(onClick = { onEvent(QuizEvent.PlayAudio) }) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Play pronunciation", tint = QuizNavy)
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    quiz.choices.forEachIndexed { index, answer ->
                        QuizChoice(
                            answer = answer, label = ('A' + index).toString(),
                            selected = state.selectedAnswer == answer,
                            correct = state.isAnswerChecked && answer == quiz.correctAnswer,
                            incorrect = state.isAnswerChecked && state.selectedAnswer == answer && answer != quiz.correctAnswer,
                            onClick = { onEvent(QuizEvent.SelectAnswer(answer)) }, enabled = !state.isAnswerChecked,
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { if (state.isAnswerChecked) onEvent(QuizEvent.Next) else onEvent(QuizEvent.CheckAnswer) },
                enabled = state.selectedAnswer != null, modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = QuizGreen),
            ) {
                Text(if (!state.isAnswerChecked) "Check answer" else if (state.currentQuestionIndex == state.quizzes.lastIndex) "Finish" else "Continue", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(10.dp)); Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun QuizChoice(answer: String, label: String, selected: Boolean, correct: Boolean, incorrect: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val border = when { correct -> QuizGreen; incorrect -> Color(0xFFBD3E32); selected -> Color(0xFF2B6CB0); else -> Color(0xFFE5E5E2) }
    val fill = when { correct -> Color(0xFFE1F6E6); incorrect -> Color(0xFFFFE9E6); selected -> Color(0xFFE8F0FF); else -> Color(0xFFF9F9F7) }
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).border(if (selected || correct || incorrect) 2.dp else 1.dp, border, RoundedCornerShape(16.dp)).clickable(enabled = enabled, onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(modifier = Modifier.size(34.dp), shape = CircleShape, color = fill) { Box(contentAlignment = Alignment.Center) { Text(label, fontWeight = FontWeight.Bold, color = QuizNavy) } }
        Spacer(Modifier.width(14.dp)); Text(answer, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = QuizNavy)
        if (correct) Surface(modifier = Modifier.size(30.dp), shape = CircleShape, color = QuizGreen) { Icon(Icons.Default.Check, contentDescription = "Correct", modifier = Modifier.padding(5.dp), tint = Color.White) }
    }
}

@Composable
fun QuizCompletedScreen(score: Int, total: Int, onContinue: () -> Unit) {
    val safeTotal = total.coerceAtLeast(1)
    val accuracy = score.toFloat() / safeTotal
    val accuracyPercent = (accuracy * 100).toInt()
    val xpEarned = score * 10

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.quiz_celebration),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter,
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(shape = CircleShape, color = Color(0xFFFFF1C7), modifier = Modifier.size(42.dp)) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.padding(9.dp),
                        tint = QuizSaffron,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("VOCABULARY QUIZ", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold, color = QuizGreen)
                    Text("Lesson checkpoint", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF526477))
                }
                Spacer(Modifier.weight(1f))
                Text("$safeTotal/$safeTotal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = QuizNavy)
            }

            Spacer(Modifier.weight(1f))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFEFFFFFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Surface(modifier = Modifier.size(78.dp), shape = CircleShape, color = Color(0xFFE1F8E8)) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Quiz completed",
                            modifier = Modifier.padding(17.dp),
                            tint = QuizGreen,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Quiz complete!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = QuizNavy)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (accuracy >= 0.8f) "Excellent recall — keep the momentum going." else "Nice effort — practice will make these words stick.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF526477),
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(22.dp))

                    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFF4F8F4), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Your accuracy", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = QuizNavy)
                                Text("$accuracyPercent%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = QuizGreen)
                            }
                            Spacer(Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { accuracy },
                                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(99.dp)),
                                color = QuizGreen,
                                trackColor = Color(0xFFD9E8DA),
                            )
                            Spacer(Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                ResultMetric("Correct", "$score", QuizGreen)
                                ResultMetric("To review", "${safeTotal - score}", QuizSaffron)
                                ResultMetric("XP earned", "+$xpEarned", QuizNavy)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QuizGreen, contentColor = Color.White),
            ) {
                Text("Continue to practice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.width(10.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
            Spacer(Modifier.height(6.dp))
            Text("Practice the words you missed anytime from Review.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF526477), textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun ResultMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = color)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF526477), textAlign = TextAlign.Center)
    }
}

private val QuizNavy = Color(0xFF082C59)
private val QuizGreen = Color(0xFF14894E)
private val QuizSaffron = Color(0xFFDD9B18)
