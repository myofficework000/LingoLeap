package com.lingoleap.presentation.feature.challenge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.DailyChallenge

data class DailyChallengeState(
    val isLoading: Boolean = true,
    val challenges: List<DailyChallenge> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerCorrect: Boolean? = null,
    val earnedXp: Int = 0,
    val error: String? = null,
) : UiState {
    val currentChallenge: DailyChallenge? get() = challenges.getOrNull(currentIndex)
    val isLastChallenge: Boolean get() = currentIndex == challenges.lastIndex
}

sealed interface DailyChallengeEvent : UiEvent {
    data object Back : DailyChallengeEvent
    data class SelectAnswer(val answer: String) : DailyChallengeEvent
    data object Next : DailyChallengeEvent
}

@Composable
fun DailyChallengeRoute(
    onBack: () -> Unit,
    onFinished: () -> Unit,
    viewModel: DailyChallengeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                DailyChallengeEffect.NavigateBack -> onBack()
                DailyChallengeEffect.Finished -> onFinished()
            }
        }
    }
    DailyChallengeScreen(state = state, onEvent = viewModel::onEvent)
}

@Composable
fun DailyChallengeScreen(
    state: DailyChallengeState,
    onEvent: (DailyChallengeEvent) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.Start),
                onClick = { onEvent(DailyChallengeEvent.Back) },
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Text("Daily Challenge", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Earn ${state.earnedXp} XP today", color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(28.dp))

            when {
                state.isLoading -> Text("Loading today’s challenges…")
                state.error != null -> Text(state.error, color = MaterialTheme.colorScheme.error)
                state.currentChallenge == null -> Text("No daily challenges are available yet.")
                else -> ChallengeQuestion(state = state, onEvent = onEvent)
            }
        }
    }
}

@Composable
private fun ChallengeQuestion(
    state: DailyChallengeState,
    onEvent: (DailyChallengeEvent) -> Unit,
) {
    val challenge = requireNotNull(state.currentChallenge)
    Text(
        text = "Challenge ${state.currentIndex + 1} of ${state.challenges.size} · ${challenge.xpReward} XP",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(Modifier.height(12.dp))
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(challenge.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Text(challenge.prompt, style = MaterialTheme.typography.headlineSmall)
        }
    }
    Spacer(Modifier.height(20.dp))
    challenge.choices.forEach { answer ->
        OutlinedButton(
            modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp),
            enabled = state.selectedAnswer == null,
            onClick = { onEvent(DailyChallengeEvent.SelectAnswer(answer)) },
        ) { Text(answer) }
        Spacer(Modifier.height(10.dp))
    }
    state.isAnswerCorrect?.let { isCorrect ->
        Text(
            text = if (isCorrect) "Correct! Great work." else "Not quite. Hint: ${challenge.hint}",
            color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onEvent(DailyChallengeEvent.Next) }) {
            Text(if (state.isLastChallenge) "Finish" else "Next challenge")
        }
    }
}
