package com.lingoleap.presentation.feature.lesson

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingoleap.domain.model.Lesson

@Composable
fun LessonsListScreen(
    onBack: () -> Unit,
    onLessonClick: (String) -> Unit,
    viewModel: LessonsListViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LessonsListContent(
        state = state,
        onBack = onBack,
        onLessonClick = onLessonClick
    )
}

@Composable
private fun LessonsListContent(
    state: LessonsListState,
    onBack: () -> Unit,
    onLessonClick: (String) -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = state.course?.title ?: "Lessons",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Lessons",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Complete lessons to unlock new topics",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            when {

                state.isLoading -> {

                    Text(
                        text = "Loading lessons..."
                    )
                }

                state.error != null -> {

                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                state.course != null -> {

                    LessonList(
                        lessons = state.course.lessons,
                        completedLessonIds =
                            state.progress?.completedLessonIds
                                ?: emptySet(),
                        onLessonClick = onLessonClick
                    )
                }
            }
        }
    }
}
@Composable
private fun LessonList(
    lessons: List<Lesson>,
    completedLessonIds: Set<String>,
    onLessonClick: (String) -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(
            items = lessons,
            key = { it.id }
        ) { lesson ->

            val isCompleted = lesson.id in completedLessonIds

            val isLocked =
                lesson.order > 1 &&
                        lessons
                            .firstOrNull {
                                it.order == lesson.order - 1
                            }
                            ?.id !in completedLessonIds

            LessonItem(
                lesson = lesson,
                isCompleted = isCompleted,
                isLocked = isLocked,
                onClick = {
                    if (!isLocked) {
                        onLessonClick(lesson.id)
                    }
                }
            )
        }
    }
}
@Composable
private fun LessonItem(
    lesson: Lesson,
    isCompleted: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        onClick = onClick,
        enabled = !isLocked
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 2.dp
            ) {

                Text(
                    text = lesson.order.toString(),
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "${lesson.words.size} words",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            when {

                isCompleted -> {

                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed"
                    )
                }

                isLocked -> {

                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked"
                    )
                }

                else -> {

                    Text(
                        text = "›",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}