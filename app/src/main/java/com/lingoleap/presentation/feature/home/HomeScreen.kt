package com.lingoleap.presentation.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(onEvent: (HomeEvent) -> Unit, viewModel: HomeViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(state = state, onEvent = onEvent)
}

@Composable
private fun HomeContent(state: HomeState, onEvent: (HomeEvent) -> Unit){

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Good Morning!",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "Keep learning, keep growing 😊",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(
                    onClick = {
                        onEvent(HomeEvent.Profile)
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile"
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )


            state.course?.let { course ->

                CourseCard(
                    course = course,
                    onClick = {
                        onEvent(HomeEvent.ContinueLearning)
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )
            state.progress?.let { progress ->

                val completedLessons =
                    progress.completedLessonIds.size

                val totalLessons =
                    state.course?.lessons?.size ?: 0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    StatCard(
                        title = "Streak",
                        value = "${progress.streakDays} days",
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Lessons",
                        value = "$completedLessons/$totalLessons",
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "XP",
                        value = progress.xp.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                onClick = {
                    onEvent(HomeEvent.ContinueLearning)
                }
            ) {
                Text("Continue Learning")
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                SmallFeatureCard(
                    title = "Games",
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )

                SmallFeatureCard(
                    title = "Practice",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onEvent(HomeEvent.Practice)
                    }
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CourseCard(
    course: com.lingoleap.domain.model.Course,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(14.dp),
                tonalElevation = 4.dp
            ) {

                Text(
                    text = "अ",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(
                modifier = Modifier.size(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Beginner",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open course"
            )
        }
    }
}
@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp)
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun SmallFeatureCard(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
