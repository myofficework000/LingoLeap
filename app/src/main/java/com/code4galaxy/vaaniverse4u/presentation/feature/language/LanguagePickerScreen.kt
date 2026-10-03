package com.code4galaxy.vaaniverse4u.presentation.feature.language

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.code4galaxy.vaaniverse4u.R
import com.code4galaxy.vaaniverse4u.core.mvi.UiEvent
import com.code4galaxy.vaaniverse4u.core.mvi.UiState
import com.code4galaxy.vaaniverse4u.domain.model.Language
import com.code4galaxy.vaaniverse4u.domain.model.LanguagePair

data class LanguagePickerState(
    val languages: List<Language> = emptyList(),
    val pairs: List<LanguagePair> = emptyList(),
    val selectedSourceId: String? = null,
    val selectedTargetId: String? = null,
) : UiState

sealed interface LanguagePickerEvent : UiEvent {
    data class SelectSource(val languageId: String) : LanguagePickerEvent
    data class SelectTarget(val languageId: String) : LanguagePickerEvent
    data object Confirm : LanguagePickerEvent
}

@Composable
fun LanguagePickerRoute(
    onConfirmed: () -> Unit,
    viewModel: LanguagePickerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { if (it is LanguagePickerEffect.Confirmed) onConfirmed() }
    }
    LanguagePickerScreen(state = state, onEvent = viewModel::onEvent)
}

/**
 * A visual course picker. Each tile represents an English <-> regional-language course;
 * internally, the existing English-to-regional pair is persisted as the course entry point.
 */
@Composable
fun LanguagePickerScreen(
    state: LanguagePickerState,
    onEvent: (LanguagePickerEvent) -> Unit,
) {
    val languageMap = state.languages.associateBy { it.id }
    val coursePairs = state.pairs.filter { it.sourceLanguageId == ENGLISH_LANGUAGE_ID }
    val selectedPairId = coursePairs.firstOrNull {
        it.sourceLanguageId == state.selectedSourceId &&
            it.targetLanguageId == state.selectedTargetId
    }?.id

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground),
    ) {
        LanguageSelectionHero()
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 154.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, top = 18.dp, end = 16.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(items = coursePairs, key = { it.id }) { pair ->
                val target = languageMap[pair.targetLanguageId]
                if (target != null) {
                    LanguageCourseTile(
                        target = target,
                        selected = pair.id == selectedPairId,
                        onClick = {
                            onEvent(LanguagePickerEvent.SelectSource(pair.sourceLanguageId))
                            onEvent(LanguagePickerEvent.SelectTarget(pair.targetLanguageId))
                            onEvent(LanguagePickerEvent.Confirm)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageSelectionHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)),
    ) {
        Image(
            painter = painterResource(R.drawable.language_pair_hero),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter,
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(start = 126.dp, top = 30.dp, end = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Let’s Learn",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ForestText,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Text(
                text = "Together",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = SaffronText,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Choose a language pair\nto start learning",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = ForestText,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun LanguageCourseTile(
    target: Language,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(184.dp)
            .semantics {
                this.selected = selected
                contentDescription = "English and ${target.name}, learn in both directions"
            }
            .border(
                width = if (selected) 3.dp else 0.dp,
                color = CourseGreen,
                shape = shape,
            )
            .clip(shape)
            .clickable(role = Role.RadioButton, onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = CardCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(languageSceneResource(target.id)),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(102.dp),
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter,
            )
            if (selected) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(28.dp),
                    shape = CircleShape,
                    color = CourseGreen,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Selected",
                        modifier = Modifier.padding(5.dp),
                        tint = Color.White,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 8.dp, vertical = 13.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SpeechBubble(text = "EN", container = EnglishNavy)
                    Icon(
                        imageVector = Icons.Outlined.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 5.dp).size(25.dp),
                        tint = ForestText,
                    )
                    SpeechBubble(text = target.nativeName.take(2), container = languageAccent(target.id))
                }
                Spacer(Modifier.height(7.dp))
                Text(
                    text = "English ↔ ${target.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Learn in both directions",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF5E6A66),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun SpeechBubble(text: String, container: Color) {
    Surface(shape = RoundedCornerShape(18.dp), color = container) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
        )
    }
}

private fun languageAccent(languageId: String): Color = when (languageId) {
    "hi", "kn" -> Color(0xFFE99712)
    "te" -> Color(0xFF468E3F)
    "ta" -> Color(0xFFC9523C)
    "ml" -> Color(0xFF863AAC)
    "bn" -> Color(0xFF1769C2)
    "mr" -> Color(0xFFC15A09)
    else -> CourseGreen
}

private fun languageSceneResource(languageId: String): Int = when (languageId) {
    "hi" -> R.drawable.scene_hindi_taj
    "kn" -> R.drawable.scene_kannada_vidhana
    "te" -> R.drawable.scene_telugu_charminar
    "ta" -> R.drawable.scene_tamil_meenakshi
    "ml" -> R.drawable.scene_malayalam_boat
    "mr" -> R.drawable.scene_marathi_fort
    "bn" -> R.drawable.scene_bengali_howrah
    "gu" -> R.drawable.scene_gujarati_stepwell
    "pa" -> R.drawable.scene_punjabi_golden_temple
    "ur" -> R.drawable.scene_urdu_jama_masjid
    "or" -> R.drawable.scene_odia_konark
    "as" -> R.drawable.scene_assamese_kaziranga
    "ne" -> R.drawable.scene_nepali_himalaya
    "sa" -> R.drawable.scene_sanskrit_temple
    else -> R.drawable.language_pair_card
}

private const val ENGLISH_LANGUAGE_ID = "en"
private val CreamBackground = Color(0xFFFFFCF4)
private val CardCream = Color(0xFFFFFDF7)
private val ForestText = Color(0xFF053E38)
private val SaffronText = Color(0xFFC8790A)
private val CourseGreen = Color(0xFF08784E)
private val EnglishNavy = Color(0xFF082D62)
