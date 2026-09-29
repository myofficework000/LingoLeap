package com.code4galaxy.lingoleap.presentation.feature.language

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.code4galaxy.lingoleap.core.mvi.UiEvent
import com.code4galaxy.lingoleap.core.mvi.UiState
import com.code4galaxy.lingoleap.domain.model.Language
import com.code4galaxy.lingoleap.domain.model.LanguagePair

data class LanguagePickerState(
    val languages: List<Language> = emptyList(),
    val pairs: List<LanguagePair> = emptyList(),
    val selectedSourceId: String? = null,
    val selectedTargetId: String? = null,
    val selectedDailyGoalMinutes: Int? = null
) : UiState

sealed interface LanguagePickerEvent : UiEvent {

    data class SelectSource(
        val languageId: String
    ) : LanguagePickerEvent

    data class SelectTarget(
        val languageId: String
    ) : LanguagePickerEvent

    data class SelectDailyGoal(
        val minutes: Int
    ) : LanguagePickerEvent

    data object Confirm : LanguagePickerEvent
}

@Composable
fun LanguagePickerRoute(
    onConfirmed: () -> Unit,
    viewModel: LanguagePickerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            if (effect is LanguagePickerEffect.Confirmed) {
                onConfirmed()
            }
        }
    }

    LanguagePickerScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun LanguagePickerScreen(
    state: LanguagePickerState,
    onEvent: (LanguagePickerEvent) -> Unit
) {

    val languageMap =
        state.languages.associateBy { it.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Choose Your Language",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Select the language you want to learn.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            items(state.pairs) { pair ->

                val sourceId =
                    pair.sourceLanguageId

                val targetId =
                    pair.targetLanguageId

                val sourceLanguage =
                    languageMap[sourceId]

                val targetLanguage =
                    languageMap[targetId]

                val isSelected =
                    state.selectedSourceId == sourceId &&
                            state.selectedTargetId == targetId

                LanguagePairCard(
                    title =
                        "${sourceLanguage?.name ?: ""} → " +
                                "${targetLanguage?.name ?: ""}",
                    subtitle =
                        "${sourceLanguage?.nativeName ?: ""} to " +
                                "${targetLanguage?.nativeName ?: ""}",
                    selected = isSelected,
                    onClick = {
                        onEvent(
                            LanguagePickerEvent.SelectSource(
                                sourceId
                            )
                        )

                        onEvent(
                            LanguagePickerEvent.SelectTarget(
                                targetId
                            )
                        )
                    }
                )
            }

            item {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Daily Learning Goal",
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        "How much time would you like to practice each day?",
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    listOf(
                        5,
                        10,
                        15,
                        20
                    ).forEach { minutes ->

                        DailyGoalCard(
                            minutes = minutes,
                            selected =
                                state.selectedDailyGoalMinutes ==
                                        minutes,
                            modifier =
                                Modifier.weight(1f),
                            onClick = {
                                onEvent(
                                    LanguagePickerEvent
                                        .SelectDailyGoal(
                                            minutes
                                        )
                                )
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {
                onEvent(
                    LanguagePickerEvent.Confirm
                )
            },
            enabled =
                state.selectedSourceId != null &&
                        state.selectedTargetId != null &&
                        state.selectedDailyGoalMinutes != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape =
                RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Continue"
            )
        }
    }
}

@Composable
fun LanguagePairCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color =
                    if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                shape =
                    RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
                .padding(16.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text = subtitle,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector =
                    Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                modifier =
                    Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun DailyGoalCard(
    minutes: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                color =
                    if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    },
                shape =
                    RoundedCornerShape(14.dp)
            )
            .clickable {
                onClick()
            },
        shape =
            RoundedCornerShape(14.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
                .padding(
                    vertical = 14.dp,
                    horizontal = 6.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "$minutes",
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = "min",
                style =
                    MaterialTheme.typography.bodySmall
            )
        }
    }
}