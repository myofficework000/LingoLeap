package com.lingoleap.presentation.feature.learningpath

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingoleap.domain.model.LearningPathNode
import com.lingoleap.domain.model.LearningPathNodeState


@Composable
fun LearningPathScreen(
    onLessonClick: (String) -> Unit,
    viewModel: LearningPathViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> {
            LearningPathLoading()
        }

        state.nodes.isEmpty() -> {
            LearningPathEmpty()
        }

        else -> {
            LearningPathContent(
                nodes = state.nodes,
                onLessonClick = onLessonClick
            )
        }
    }
}


@Composable
fun LearningPathLoading() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun LearningPathEmpty() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "No lessons available yet.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun LearningPathContent(
    nodes: List<LearningPathNode>,
    onLessonClick: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            )
    ) {

        Text(
            text = "Learning Path",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Complete lessons to unlock the next step.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            itemsIndexed(
                items = nodes,
                key = { _, node ->
                    node.lesson.id
                }
            ) { index, node ->

                LearningPathItem(
                    node = node,
                    index = index,
                    isLastItem = index == nodes.lastIndex,
                    onLessonClick = onLessonClick
                )
            }
        }
    }
}


@Composable
fun LearningPathItem(
    node: LearningPathNode,
    index: Int,
    isLastItem: Boolean,
    onLessonClick: (String) -> Unit
) {

    val alignStart = index % 2 == 0

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                if (alignStart) {
                    Arrangement.Start
                } else {
                    Arrangement.End
                }
        ) {

            LearningPathNodeCard(
                node = node,
                onLessonClick = onLessonClick
            )
        }

        if (!isLastItem) {
            LearningPathConnector(
                alignStart = alignStart,
                state = node.state
            )
        }
    }
}


@Composable
fun LearningPathNodeCard(
    node: LearningPathNode,
    onLessonClick: (String) -> Unit
) {

    val isLocked =
        node.state == LearningPathNodeState.LOCKED

    val nodeAlpha =
        if (isLocked) {
            0.45f
        } else {
            1f
        }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.72f)
            .alpha(nodeAlpha)
            .clickable(
                enabled = !isLocked
            ) {
                onLessonClick(node.lesson.id)
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                when (node.state) {

                    LearningPathNodeState.COMPLETED ->
                        MaterialTheme.colorScheme.secondaryContainer

                    LearningPathNodeState.CURRENT ->
                        MaterialTheme.colorScheme.primaryContainer

                    LearningPathNodeState.LOCKED ->
                        MaterialTheme.colorScheme.surfaceVariant
                }
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LearningPathBadge(
                state = node.state
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = node.lesson.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    when (node.state) {

                        LearningPathNodeState.COMPLETED ->
                            "Completed"

                        LearningPathNodeState.CURRENT ->
                            "Start lesson"

                        LearningPathNodeState.LOCKED ->
                            "Complete previous lesson first"
                    },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun LearningPathBadge(
    state: LearningPathNodeState
) {

    Box(
        modifier = Modifier
            .size(64.dp)
            .background(
                color =
                    when (state) {

                        LearningPathNodeState.COMPLETED ->
                            MaterialTheme.colorScheme.secondary

                        LearningPathNodeState.CURRENT ->
                            MaterialTheme.colorScheme.primary

                        LearningPathNodeState.LOCKED ->
                            MaterialTheme.colorScheme.outlineVariant
                    },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text =
                when (state) {

                    LearningPathNodeState.COMPLETED ->
                        "✓"

                    LearningPathNodeState.CURRENT ->
                        "▶"

                    LearningPathNodeState.LOCKED ->
                        "🔒"
                },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun LearningPathConnector(
    alignStart: Boolean,
    state: LearningPathNodeState
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement =
            if (alignStart) {
                Arrangement.Start
            } else {
                Arrangement.End
            }
    ) {

        Box(
            modifier = Modifier
                .padding(
                    start = if (alignStart) 85.dp else 0.dp,
                    end = if (alignStart) 0.dp else 85.dp
                )
                .size(
                    width = 4.dp,
                    height = 48.dp
                )
                .background(
                    color =
                        if (state == LearningPathNodeState.COMPLETED) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}