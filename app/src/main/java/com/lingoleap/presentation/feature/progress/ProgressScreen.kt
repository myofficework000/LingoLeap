package com.lingoleap.presentation.feature.progress

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.LearnerProgress

enum class ProgressRange{
    WEEKLY,
    MONTHLY,
    OVERALL
}

data class ProgressState(
    val progress: LearnerProgress? = null,
    val weeklyXp: List<Int> = emptyList(),
    val selectedRange: ProgressRange = ProgressRange.WEEKLY
) : UiState

sealed interface ProgressEvent : UiEvent {
    data object Refresh : ProgressEvent;
    data object OpenAchievements : ProgressEvent

    data class SelectRange(val range: ProgressRange): ProgressEvent
}

@Composable fun ProgressScreen(
    state: ProgressState = ProgressState(),
    onEvent: (ProgressEvent) -> Unit
) {
    ProgressSummary(
        progress = state.progress,
        weeklyXp = state.weeklyXp,
        selectedRange = state.selectedRange,
        onEvent = onEvent
    )
}
@Composable fun ProgressSummary(
    progress: LearnerProgress?,
    weeklyXp: List<Int>,
    selectedRange: ProgressRange,
    onEvent: (ProgressEvent) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 24.dp
            )
    ) {

        Text(
            text = "Your Progress",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer( modifier = Modifier.height(20.dp))

        ProgressRangeSelector(
            selectedRange = selectedRange,
            onRangeSelected = { range ->
                onEvent(
                    ProgressEvent.SelectRange(range)
                )
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        WeeklyXpChart(weeklyXp = weeklyXp)

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        ProgressStats(progress = progress )

        Spacer(modifier = Modifier.height(28.dp))

        MotivationCard()

        Spacer(modifier = Modifier.height(20.dp) )

        Button(
            onClick = {
                onEvent(ProgressEvent.OpenAchievements)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text(
                text = "View Achievements",
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }

}

@Composable
fun MotivationCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "“Great job!”",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Keep going!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ProgressStats(
    progress: LearnerProgress?
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        ProgressStatCard(
            modifier = Modifier.weight(1f),
            title = "Lessons",
            value = progress?.completedLessonIds?.size?.toString() ?: "0"
        )

        ProgressStatCard(
            modifier = Modifier.weight(1f),
            title = "XP",
            value = progress?.xp?.toString() ?: "0"
        )

        ProgressStatCard(
            modifier = Modifier.weight(1f),
            title = "Streak",
            value = "${progress?.streakDays ?: 0} days"
        )
    }
}

@Composable
fun ProgressStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun WeeklyXpChart(weeklyXp: List<Int>) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val maxXp = weeklyXp.maxOrNull() ?: 1

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Weekly Xp",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .height(160.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            weeklyXp.forEachIndexed { index, xp ->
                val percentage = xp.toFloat() / maxXp.toFloat()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "$xp",
                        style = MaterialTheme.typography.labelSmall
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height(
                                (110 * percentage).dp
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStart = 6.dp,
                                    topEnd = 6.dp
                                )
                            )
                            .background(
                                MaterialTheme.colorScheme.primary
                            )
                    )
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = days[index],
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressRangeSelector(selectedRange: ProgressRange, onRangeSelected: (ProgressRange) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp)
        )
        .padding(4.dp)) {
        ProgressRange.entries.forEach { range ->
            val isSelected = selectedRange == range

            val backgroundColor =
                if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                }

            val textColor =
                if (isSelected) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }


            Box(modifier = Modifier.weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(backgroundColor)
                .clickable{
                    onRangeSelected(range)
                }
                .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center){

                Text(
                    text = when(range) {
                        ProgressRange.WEEKLY -> "Weekly"
                        ProgressRange.MONTHLY -> "Monthly"
                        ProgressRange.OVERALL -> "OverAll"
                    },
                    color = textColor,
                    fontWeight = if (isSelected){
                        FontWeight.SemiBold
                    } else{
                        FontWeight.Normal
                    }
                )
            }
        }
    }
}
