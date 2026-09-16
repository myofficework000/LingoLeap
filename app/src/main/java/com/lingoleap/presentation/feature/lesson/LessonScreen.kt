package com.lingoleap.presentation.feature.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.lingoleap.domain.usecase.LessonRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@Composable
fun LessonRoute(lessonId: String, onCompleted: () -> Unit, viewModel: LessonViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) {
        viewModel.load(lessonId)
    }

    LaunchedEffect(viewModel) {

        viewModel.effect.collect { effect ->
            when (effect) {
                is LessonEffect.Completed -> {
                    onCompleted()
                }
                is LessonEffect.ShowMessage -> {

                }
            }
        }
    }

    LessonScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun LessonScreen(state: LessonState, onEvent: (LessonEvent) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            IconButton(
                onClick = { onEvent(LessonEvent.Previous) },
                enabled = !state.isFirstWord
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous word"
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = state.lesson?.title ?: "Lesson",
                    style = MaterialTheme.typography.titleLarge)

                Text(
                    text =
                        if (state.totalWords > 0) { "${state.wordIndex + 1} of ${state.totalWords}"
                        }
                        else {"" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(48.dp)
        )

        val word = state.currentWord
        if (word != null) {
            Text(
                text = word.sourceText,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = word.targetText,
                style =
                    MaterialTheme.typography
                        .displaySmall
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            word.transliteration?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            OutlinedButton(
                onClick = { onEvent(LessonEvent.PlayAudio) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = " Play pronunciation")
            }

            state.audioError?.let { message ->

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

        } else if (state.error != null) {

            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            OutlinedButton(
                modifier =
                    Modifier.weight(1f),
                enabled =
                    !state.isFirstWord,
                onClick = {
                    onEvent(
                        LessonEvent.Previous
                    )
                }
            ) {

                Text("Previous")
            }

            Button(
                modifier =
                    Modifier.weight(1f),
                enabled =
                    word != null &&
                            !state.isCompleting,
                onClick = {

                    if (state.isLastWord) {

                        onEvent(
                            LessonEvent.Finish
                        )

                    } else {

                        onEvent(
                            LessonEvent.Next
                        )
                    }
                }
            ) {

                Text(
                    if (state.isLastWord) {
                        if (state.canComplete) {
                            "Complete"
                        } else {
                            "Review"
                        }
                    } else {
                        "Next"
                    }
                )
            }
        }
    }
}