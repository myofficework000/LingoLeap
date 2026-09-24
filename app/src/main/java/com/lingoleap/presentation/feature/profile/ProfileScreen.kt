package com.lingoleap.presentation.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState

data class ProfileState(val displayName: String = "Learner", val selectedCourseCount: Int = 0) : UiState
sealed interface ProfileEvent : UiEvent {
    data object OpenLanguages : ProfileEvent

    data object OpenStatistics : ProfileEvent

    data object OpenAchievements : ProfileEvent

    data object OpenSettings : ProfileEvent

    data object OpenHelpSupport : ProfileEvent

    data object SignOut : ProfileEvent

}

sealed interface ProfileEffect {

    data object NavigateToLanguages : ProfileEffect
    data object NavigateToStatistics : ProfileEffect
    data object NavigateToAchievements : ProfileEffect
    data object NavigateToSettings : ProfileEffect
    data object NavigateToHelpSupport : ProfileEffect
    data object SignOut : ProfileEffect
}
@Composable fun ProfileScreen(state: ProfileState = ProfileState(), onEvent: (ProfileEvent) -> Unit) { ProfileContent(state = state, onEvent = onEvent) }
@Composable
fun ProfileContent(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(20.dp)
    ) {

        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        ProfileHeader(
            displayName = state.displayName
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        ProfileMenuItem(
            title = "My Languages",
            icon = Icons.Default.Language,
            onClick = {
                onEvent(ProfileEvent.OpenLanguages)
            }
        )

        HorizontalDivider()

        ProfileMenuItem(
            title = "Statistics",
            icon = Icons.Default.BarChart,
            onClick = {
                onEvent(ProfileEvent.OpenStatistics)
            }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.5f
            )

        )

        ProfileMenuItem(
            title = "Achievements",
            icon = Icons.Default.EmojiEvents,
            onClick = {
                onEvent(ProfileEvent.OpenAchievements)
            }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.5f
            )

        )

        ProfileMenuItem(
            title = "Settings",
            icon = Icons.Default.Settings,
            onClick = {
                onEvent(ProfileEvent.OpenSettings)
            }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.5f
            )

        )

        ProfileMenuItem(
            title = "Help & Support",
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            onClick = {
                onEvent(ProfileEvent.OpenHelpSupport)
            }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.5f
            )

        )

        ProfileMenuItem(
            title = "Restart setup",
            icon = Icons.AutoMirrored.Filled.Logout,
            iconColor = MaterialTheme.colorScheme.error,
            textColor = MaterialTheme.colorScheme.error,
            onClick = {
                onEvent(ProfileEvent.SignOut)
            }
        )
    }
}



@Composable
fun ProfileHeader(
    displayName: String
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(46.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = displayName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Beginner Learner",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun ProfileMenuItem(
    title: String,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
