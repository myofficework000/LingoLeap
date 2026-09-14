package com.lingoleap.presentation.feature.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.VocabularyWord

data class PracticeState(
    val words: List<VocabularyWord> = emptyList(),
    val selectedMode: PracticeMode = PracticeMode.WORD_MATCH,
    val selectedLeftWord: VocabularyWord? = null,
    val selectedRightWord: VocabularyWord? = null,
    val matchedWordIds: Set<String> = emptySet(),
    val isWrongMatch: Boolean = false
) : UiState

enum class PracticeMode { WORD_MATCH, LISTENING, FILL_BLANK }
sealed interface PracticeEvent : UiEvent {
    data class SelectMode(
        val mode: PracticeMode
    ) : PracticeEvent

    data class SelectLeft(
        val word: VocabularyWord
    ) : PracticeEvent

    data class SelectRight(
        val word: VocabularyWord
    ) : PracticeEvent

    data object Next : PracticeEvent
}

@Composable
fun PracticeRoute(
    lessonId: String,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) {
        viewModel.loadPractice(lessonId)
    }

    PracticeScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun PracticeScreen(
    state: PracticeState,
    onEvent: (PracticeEvent) -> Unit
) {
    when (state.selectedMode) {

        PracticeMode.WORD_MATCH -> {
            WordMatchScreen(
                state = state,
                onEvent = onEvent
            )
        }

        PracticeMode.LISTENING -> {
            Text("Listening practice coming next")
        }

        PracticeMode.FILL_BLANK -> {
            Text("Fill in the blank coming next")
        }
    }
}

@Composable
fun WordMatchScreen(
    state: PracticeState,
    onEvent: (PracticeEvent) -> Unit
) {
    val activeWords = state.words.filterNot {
        state.matchedWordIds.contains(it.id)
    }

    val rightWords = remember(state.words) {
        state.words.shuffled()
    }

    val isCompleted =
        state.words.isNotEmpty() &&
                state.matchedWordIds.size == state.words.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Word Match",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Match the pairs"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                activeWords.forEach { word ->

                    val isSelected =
                        state.selectedLeftWord?.id == word.id

                    OutlinedButton(
                        onClick = {
                            onEvent(
                                PracticeEvent.SelectLeft(word)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) {
                                androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                androidx.compose.material3.MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Text(word.sourceText)
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                rightWords
                    .filterNot { state.matchedWordIds.contains(it.id) }
                    .forEach { word ->

                        val isSelected =
                            state.selectedRightWord?.id == word.id
                        OutlinedButton(
                            onClick = {
                                onEvent(
                                    PracticeEvent.SelectRight(word)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) {
                                androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                androidx.compose.material3.MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Text(word.targetText)
                        }
                    }
            }

            if (state.isWrongMatch) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Wrong match. Try again.",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error
                )
            }


        }

        if (isCompleted) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Great job!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You matched all the words."
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onEvent(PracticeEvent.Next)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WordMatchPreview() {

    val fakeWords = listOf(
        VocabularyWord(
            id = "1",
            sourceText = "పుస్తకం",
            targetText = "Book"
        ),
        VocabularyWord(
            id = "2",
            sourceText = "నీరు",
            targetText = "Water"
        ),
        VocabularyWord(
            id = "3",
            sourceText = "ఇల్లు",
            targetText = "House"
        ),
        VocabularyWord(
            id = "4",
            sourceText = "అమ్మ",
            targetText = "Mother"
        )
    )

    PracticeScreen(
        state = PracticeState(
            words = fakeWords,
            selectedMode = PracticeMode.WORD_MATCH
        ),
        onEvent = {}
    )
}
