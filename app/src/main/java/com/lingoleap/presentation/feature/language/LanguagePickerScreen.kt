package com.lingoleap.presentation.feature.language

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.Language
import com.lingoleap.domain.model.LanguagePair

data class LanguagePickerState(
    val languages: List<Language> = emptyList(),
    val pairs: List<LanguagePair> = emptyList(),
    val selectedSourceId: String? = null,
    val selectedTargetId: String? = null
) : UiState

sealed interface LanguagePickerEvent : UiEvent {
    data class SelectSource(val languageId: String) : LanguagePickerEvent
    data class SelectTarget(val languageId: String) : LanguagePickerEvent
    data object Confirm : LanguagePickerEvent
}

@Composable
fun LanguagePickerRoute(
    onConfirmed: () -> Unit,
    viewModel: LanguagePickerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { if (it is LanguagePickerEffect.Confirmed) onConfirmed() }
    }

    LanguagePickerScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun LanguagePickerScreen(
    state: LanguagePickerState,
    onEvent: (LanguagePickerEvent) -> Unit
) {
    val languageMap = state.languages.associateBy { it.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Choose Your Language",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select the language you want to learn.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.pairs) { pair ->

                // Change property names here if your model differs
                val sourceId = pair.sourceLanguageId
                val targetId = pair.targetLanguageId

                val sourceLanguage = languageMap[sourceId]
                val targetLanguage = languageMap[targetId]

                val isSelected =
                    state.selectedSourceId == sourceId &&
                            state.selectedTargetId == targetId

                LanguagePairCard(
                    title = "${sourceLanguage?.name ?: ""} → ${targetLanguage?.name ?: ""}",
                    subtitle = "${sourceLanguage?.nativeName ?: ""} to ${targetLanguage?.nativeName ?: ""}",
                    selected = isSelected,
                    onClick = {
                        onEvent(LanguagePickerEvent.SelectSource(sourceId))
                        onEvent(LanguagePickerEvent.SelectTarget(targetId))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onEvent(LanguagePickerEvent.Confirm) },
            enabled = state.selectedSourceId != null && state.selectedTargetId != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text("Continue")
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
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
