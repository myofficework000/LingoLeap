package com.lingoleap.presentation.feature.learningpath

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

    val completedCount =
        nodes.count { node ->
            node.state == LearningPathNodeState.COMPLETED
        }

    val totalCount = nodes.size

    val progress =
        if (totalCount > 0) {
            completedCount.toFloat() / totalCount.toFloat()
        } else {
            0f
        }

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
            text = "$completedCount of $totalCount lessons completed",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        LinearProgressIndicator(
            progress = {
                progress.coerceIn(0f, 1f)
            },
            modifier = Modifier.fillMaxWidth()
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

    val isCheckpoint =
        (index + 1) % 3 == 0

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                if (alignStart) {
                    Arrangement.Start
                } else {
                    Arrangement.End
                }
        ) {

            if (alignStart) {

                LearningPathNodeCircle(
                    node = node,
                    isCheckpoint = isCheckpoint,
                    onLessonClick = onLessonClick
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                LearningPathLessonInfo(
                    node = node,
                    isCheckpoint = isCheckpoint,
                    modifier = Modifier.weight(1f)
                )

            } else {

                LearningPathLessonInfo(
                    node = node,
                    isCheckpoint = isCheckpoint,
                    modifier = Modifier.weight(1f)
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                LearningPathNodeCircle(
                    node = node,
                    isCheckpoint = isCheckpoint,
                    onLessonClick = onLessonClick
                )
            }
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
fun LearningPathNodeCircle(
    node: LearningPathNode,
    isCheckpoint: Boolean,
    onLessonClick: (String) -> Unit
) {

    val isLocked =
        node.state == LearningPathNodeState.LOCKED

    val nodeSize =
        when {

            isCheckpoint -> {
                112.dp
            }

            node.state == LearningPathNodeState.CURRENT -> {
                100.dp
            }

            else -> {
                84.dp
            }
        }


    val borderWidth =
        if (node.state == LearningPathNodeState.CURRENT) {
            4.dp
        } else {
            0.dp
        }

    val nodeAlpha =
        if (isLocked) {
            0.55f
        } else {
            1f
        }


    val infiniteTransition =
        rememberInfiniteTransition(
            label = "currentNodePulse"
        )

    val pulseScale =
        if (node.state == LearningPathNodeState.CURRENT) {

            infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 900
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "currentNodeScale"
            ).value

        } else {
            1f
        }

    Box(
        modifier = Modifier
            .size(nodeSize)
            .scale(pulseScale)
            .alpha(nodeAlpha)
            .border(
                width = borderWidth,
                color =
                    if (
                        node.state ==
                        LearningPathNodeState.CURRENT
                    ) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                shape = CircleShape
            )
            .padding(
                if (
                    node.state ==
                    LearningPathNodeState.CURRENT
                ) {
                    6.dp
                } else {
                    0.dp
                }
            )
            .background(
                color =
                    when (node.state) {

                        LearningPathNodeState.COMPLETED ->
                            MaterialTheme.colorScheme.primary

                        LearningPathNodeState.CURRENT ->
                            MaterialTheme.colorScheme.primaryContainer

                        LearningPathNodeState.LOCKED ->
                            MaterialTheme.colorScheme.outlineVariant
                    },
                shape = CircleShape
            )
            .clickable(
                enabled = !isLocked
            ) {

                onLessonClick(
                    node.lesson.id
                )
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text =
                when {

                    isCheckpoint &&
                            node.state ==
                            LearningPathNodeState.COMPLETED -> {
                        "🏆"
                    }

                    isCheckpoint &&
                            node.state ==
                            LearningPathNodeState.CURRENT -> {
                        "⭐"
                    }

                    isCheckpoint &&
                            node.state ==
                            LearningPathNodeState.LOCKED -> {
                        "🔒"
                    }
                    node.state ==
                            LearningPathNodeState.COMPLETED -> {
                        "✓"
                    }

                    node.state ==
                            LearningPathNodeState.CURRENT -> {
                        "▶"
                    }

                    else -> {
                        "🔒"
                    }
                },
            style =
                if (
                    isCheckpoint ||
                    node.state ==
                    LearningPathNodeState.CURRENT
                ) {
                    MaterialTheme.typography.headlineLarge
                } else {
                    MaterialTheme.typography.headlineMedium
                },
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun LearningPathLessonInfo(
    node: LearningPathNode,
    isCheckpoint: Boolean,
    modifier: Modifier = Modifier
) {

    val isLocked =
        node.state == LearningPathNodeState.LOCKED

    Card(
        modifier = modifier
            .alpha(
                if (isLocked) {
                    0.65f
                } else {
                    1f
                }
            ),
        shape = RoundedCornerShape(16.dp),
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
            modifier = Modifier.padding(16.dp)
        ) {

            if (isCheckpoint) {

                Text(
                    text = "CHECKPOINT",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            }

            Text(
                text = node.lesson.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    when (node.state) {

                        LearningPathNodeState.COMPLETED -> {
                            "Completed"
                        }

                        LearningPathNodeState.CURRENT -> {

                            if (isCheckpoint) {
                                "Current checkpoint"
                            } else {
                                "Current lesson"
                            }
                        }

                        LearningPathNodeState.LOCKED -> {

                            if (isCheckpoint) {
                                "Checkpoint locked"
                            } else {
                                "Complete previous lesson first"
                            }
                        }
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun LearningPathConnector(
    alignStart: Boolean,
    state: LearningPathNodeState
) {

    val connectorColor =
        if (
            state ==
            LearningPathNodeState.COMPLETED
        ) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
    ) {

        val startX =
            if (alignStart) {
                size.width * 0.16f
            } else {
                size.width * 0.84f
            }

        val endX =
            if (alignStart) {
                size.width * 0.84f
            } else {
                size.width * 0.16f
            }

        val startY = 0f
        val endY = size.height

        val path =
            Path().apply {

                moveTo(
                    x = startX,
                    y = startY
                )

                cubicTo(
                    x1 = startX,
                    y1 = size.height * 0.35f,

                    x2 = endX,
                    y2 = size.height * 0.65f,

                    x3 = endX,
                    y3 = endY
                )
            }

        drawPath(
            path = path,
            color = connectorColor,
            style = Stroke(
                width = 8f,
                cap = StrokeCap.Round
            )
        )
    }
}