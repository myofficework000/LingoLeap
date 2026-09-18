package com.lingoleap.presentation.feature.learningpath

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingoleap.domain.model.LearningPathNode
import com.lingoleap.domain.model.LearningPathNodeState

@Composable
fun LearningPathRoute(
    onBack: () -> Unit,
    onOpenLesson: (String) -> Unit,
    viewModel: LearningPathViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LearningPathEffect.NavigateBack -> onBack()
                is LearningPathEffect.NavigateToLesson -> onOpenLesson(effect.lessonId)
            }
        }
    }
    LearningPathScreen(state = state, onEvent = viewModel::onEvent)
}

@Composable
fun LearningPathScreen(
    state: LearningPathState,
    onEvent: (LearningPathEvent) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onEvent(LearningPathEvent.Back) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Your learning path", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(state.courseTitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(18.dp))
            when {
                state.isLoading -> Text("Preparing your path…")
                state.error != null -> Text(state.error, color = MaterialTheme.colorScheme.error)
                state.nodes.isEmpty() -> Text("Choose a language to start your learning path.")
                else -> LearningPathList(nodes = state.nodes, onEvent = onEvent)
            }
        }
    }
}

@Composable
private fun LearningPathList(nodes: List<LearningPathNode>, onEvent: (LearningPathEvent) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items(nodes, key = { it.lesson.id }) { node ->
            val selectable = node.state != LearningPathNodeState.LOCKED
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { if (selectable) onEvent(LearningPathEvent.SelectLesson(node.lesson.id)) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (node.state == LearningPathNodeState.CURRENT) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    PathStatusIcon(node.state)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("${node.lesson.order}. ${node.lesson.title}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = when (node.state) {
                                LearningPathNodeState.COMPLETED -> "Completed"
                                LearningPathNodeState.CURRENT -> "Ready to learn"
                                LearningPathNodeState.LOCKED -> "Complete earlier lessons to unlock"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text("${node.lesson.words.size} words", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun PathStatusIcon(state: LearningPathNodeState) {
    val icon = when (state) {
        LearningPathNodeState.COMPLETED -> Icons.Default.Check
        LearningPathNodeState.CURRENT -> Icons.Default.PlayArrow
        LearningPathNodeState.LOCKED -> Icons.Default.Lock
    }
    val color = when (state) {
        LearningPathNodeState.COMPLETED -> MaterialTheme.colorScheme.primary
        LearningPathNodeState.CURRENT -> MaterialTheme.colorScheme.tertiary
        LearningPathNodeState.LOCKED -> MaterialTheme.colorScheme.outline
    }
    Surface(modifier = Modifier.size(44.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), shape = CircleShape) {
        Icon(icon, contentDescription = null, modifier = Modifier.padding(10.dp), tint = color)
    }
}
