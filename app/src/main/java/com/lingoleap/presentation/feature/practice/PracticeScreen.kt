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
import android.speech.tts.TextToSpeech
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

data class PracticeState(
    val words: List<VocabularyWord> = emptyList(),
    val selectedMode: PracticeMode = PracticeMode.WORD_MATCH,

    // Word Match
    val selectedLeftWord: VocabularyWord? = null,
    val selectedRightWord: VocabularyWord? = null,
    val matchedWordIds: Set<String> = emptySet(),
    val isWrongMatch: Boolean = false,

    // Listening
    val listeningWordIndex: Int = 0,
    val selectedListeningAnswer: String? = null,
    val isListeningAnswerChecked: Boolean = false,

    val sourceLanguageId: String? = null,

    // Fill in the Blank
    val fillBlankWordIndex: Int = 0,
    val fillBlankAnswer: String = "",
    val isFillBlankChecked: Boolean = false,

    // Overall completion
    val isPracticeCompleted: Boolean = false
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

    data class SelectListeningAnswer(
        val answer: String
    ) : PracticeEvent

    data object CheckListeningAnswer : PracticeEvent

    data object NextListeningWord : PracticeEvent

    data object Next : PracticeEvent

    data class FillBlankAnswerChanged(
        val answer: String
    ) : PracticeEvent

    data object CheckFillBlankAnswer : PracticeEvent

    data object NextFillBlankWord : PracticeEvent
}

@Composable
fun PracticeRoute(
    lessonId: String,
    onFinished: () -> Unit,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) {
        viewModel.loadPractice(lessonId)
    }

    PracticeScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onFinished = onFinished
    )
}

@Composable
fun PracticeScreen(
    state: PracticeState,
    onEvent: (PracticeEvent) -> Unit,
    onFinished: () -> Unit
) {
    when (state.selectedMode) {

        PracticeMode.WORD_MATCH -> {
            WordMatchScreen(
                state = state,
                onEvent = onEvent
            )
        }

        PracticeMode.LISTENING -> {
            ListeningPracticeScreen(
                state = state,
                sourceLanguageId = state.sourceLanguageId,
                onEvent = onEvent
            )
        }

        PracticeMode.FILL_BLANK -> {

            if (state.isPracticeCompleted) {

                PracticeCompleteScreen(
                    onFinished = onFinished
                )

            } else {

                FillBlankPracticeScreen(
                    state = state,
                    onEvent = onEvent
                )
            }
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

        }

        if (state.isWrongMatch) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Wrong match. Try again.",
                color = androidx.compose.material3.MaterialTheme.colorScheme.error
            )
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

@Composable
fun ListeningPracticeScreen(
    state: PracticeState,
    sourceLanguageId: String?,
    onEvent: (PracticeEvent) -> Unit
) {
    val currentWord =
        state.words.getOrNull(
            state.listeningWordIndex
        )

    if (currentWord == null) {
        Text("No words available")
        return
    }

    val context = LocalContext.current

    var textToSpeech by remember {
        mutableStateOf<TextToSpeech?>(null)
    }

    var ttsInitStatus by remember {
        mutableStateOf<Int?>(null)
    }

    var isTtsReady by remember {
        mutableStateOf(false)
    }

    var isLanguageAvailable by remember {
        mutableStateOf(true)
    }

    val sourceLocale =
        languageIdToLocale(sourceLanguageId)

    DisposableEffect(context) {

        val engine = TextToSpeech(context) { status ->
            ttsInitStatus = status
        }

        textToSpeech = engine

        onDispose {
            engine.stop()
            engine.shutdown()
            textToSpeech = null
        }
    }

    LaunchedEffect(
        ttsInitStatus,
        textToSpeech
    ) {

        if (ttsInitStatus == TextToSpeech.SUCCESS) {

            val result =
                textToSpeech?.setLanguage(
                    sourceLocale
                )

            isLanguageAvailable =
                result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED

            isTtsReady =
                isLanguageAvailable

        } else if (ttsInitStatus != null) {

            isTtsReady = false
        }
    }

    val options = remember(
        currentWord,
        state.words
    ) {
        state.words
            .map { it.targetText }
            .shuffled()
    }

    val isCorrect =
        state.selectedListeningAnswer ==
                currentWord.targetText

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Listening Practice",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Listen and choose the correct meaning"
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Button(
            onClick = {
                if (isTtsReady) {
                    textToSpeech?.speak(
                        currentWord.sourceText,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        currentWord.id
                    )
                }
            },
            enabled =
                isTtsReady &&
                        !(state.isListeningAnswerChecked && isCorrect),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF22C55E)
            )
        ) {
            Text("Listen")
        }

        if (!isLanguageAvailable) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Speech is not available for the selected language on this device.",
                color = Color.Red
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        options.forEach { option ->

            val isSelected =
                state.selectedListeningAnswer == option

            val isCorrectOption =
                option == currentWord.targetText

            val buttonColor =
                when {

                    state.isListeningAnswerChecked &&
                            isCorrectOption -> {
                        Color(0xFFD1FAE5)
                    }

                    state.isListeningAnswerChecked &&
                            isSelected -> {
                        Color(0xFFFEE2E2)
                    }

                    isSelected -> {
                        Color(0xFFE0F2FE)
                    }

                    else -> {
                        Color.White
                    }
                }

            OutlinedButton(
                onClick = {

                    onEvent(
                        PracticeEvent.SelectListeningAnswer(
                            option
                        )
                    )
                },
                enabled =
                    !state.isListeningAnswerChecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = buttonColor
                )
            ) {

                Text(option)
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (state.isListeningAnswerChecked) {

            Text(
                text =
                    if (isCorrect) {
                        "Correct!"
                    } else {
                        "Incorrect. Correct answer: ${currentWord.targetText}"
                    },
                color =
                    if (isCorrect) {
                        Color(0xFF16A34A)
                    } else {
                        Color.Red
                    }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = {
                    onEvent(
                        PracticeEvent.NextListeningWord
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFF22C55E)
                    )
            ) {
                Text("Next")
            }

        } else {

            Button(
                onClick = {
                    onEvent(
                        PracticeEvent.CheckListeningAnswer
                    )
                },
                enabled =
                    state.selectedListeningAnswer != null,
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFF22C55E)
                    )
            ) {
                Text("Check Answer")
            }
        }
    }
}

@Composable
fun FillBlankPracticeScreen(
    state: PracticeState,
    onEvent: (PracticeEvent) -> Unit
) {
    val currentWord =
        state.words.getOrNull(
            state.fillBlankWordIndex
        )

    if (currentWord == null) {
        Text("No words available")
        return
    }

    val userAnswer =
        state.fillBlankAnswer.trim()

    val nativeAnswer =
        currentWord.targetText.trim()

    val transliterationAnswer =
        currentWord.transliteration?.trim()

    val isCorrect =
        userAnswer.equals(
            nativeAnswer,
            ignoreCase = true
        ) ||
                (
                        !transliterationAnswer.isNullOrBlank() &&
                                userAnswer.equals(
                                    transliterationAnswer,
                                    ignoreCase = true
                                )
                        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Fill in the Blank",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Type the correct meaning"
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = currentWord.sourceText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = state.fillBlankAnswer,
            onValueChange = { answer ->
                onEvent(
                    PracticeEvent.FillBlankAnswerChanged(
                        answer
                    )
                )
            },
            enabled = !state.isFillBlankChecked,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Your answer")
            },
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (state.isFillBlankChecked) {

            Text(
                text =
                    if (!transliterationAnswer.isNullOrBlank()) {
                        "${currentWord.targetText} / $transliterationAnswer"
                    } else {
                        currentWord.targetText
                    },
                color =
                    if (isCorrect) {
                        Color(0xFF16A34A)
                    } else {
                        Color.Red
                    }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = {
                    onEvent(
                        PracticeEvent.NextFillBlankWord
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E)
                )
            ) {
                Text("Next")
            }

        } else {

            Button(
                onClick = {
                    onEvent(
                        PracticeEvent.CheckFillBlankAnswer
                    )
                },
                enabled =
                    state.fillBlankAnswer.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E)
                )
            ) {
                Text("Check Answer")
            }
        }
    }
}

@Composable
fun PracticeCompleteScreen(
    onFinished: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Practice Complete!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Great job! You completed all practice activities."
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onFinished,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF22C55E)
            )
        ) {
            Text("Done")
        }
    }
}
private fun languageIdToLocale(
    languageId: String?
): Locale {
    return when (languageId) {
        "te" -> Locale.forLanguageTag("te-IN")
        "hi" -> Locale.forLanguageTag("hi-IN")
        "ta" -> Locale.forLanguageTag("ta-IN")
        "kn" -> Locale.forLanguageTag("kn-IN")
        "ml" -> Locale.forLanguageTag("ml-IN")
        "en" -> Locale.forLanguageTag("en-US")
        else -> Locale.getDefault()
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
        onEvent = {},
        onFinished = {}
    )
}
