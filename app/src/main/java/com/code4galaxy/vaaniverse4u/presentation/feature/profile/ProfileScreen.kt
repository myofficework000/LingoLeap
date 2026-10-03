package com.code4galaxy.vaaniverse4u.presentation.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.code4galaxy.vaaniverse4u.R
import com.code4galaxy.vaaniverse4u.core.mvi.UiEvent
import com.code4galaxy.vaaniverse4u.core.mvi.UiState

private val ProfileNavy = Color(0xFF082C59)
private val ProfileGreen = Color(0xFF08784E)
private val ProfileMuted = Color(0xFF5B6D7F)

data class ProfileState(val displayName: String = "Learner", val selectedCourseCount: Int = 0) : UiState

sealed interface ProfileEvent : UiEvent {
    data object OpenLanguages : ProfileEvent
    data object OpenStatistics : ProfileEvent
    data object OpenAchievements : ProfileEvent
    data object OpenSettings : ProfileEvent
    data object OpenHelpSupport : ProfileEvent
    data object OpenAccount : ProfileEvent
    data object SignOut : ProfileEvent
}

sealed interface ProfileEffect {
    data object NavigateToLanguages : ProfileEffect
    data object NavigateToStatistics : ProfileEffect
    data object NavigateToAchievements : ProfileEffect
    data object NavigateToSettings : ProfileEffect
    data object NavigateToHelpSupport : ProfileEffect
    data object NavigateToAccount : ProfileEffect
    data object SignOut : ProfileEffect
}

@Composable
fun ProfileScreen(state: ProfileState = ProfileState(), onEvent: (ProfileEvent) -> Unit) = ProfileContent(state, onEvent)

@Composable
fun ProfileContent(state: ProfileState, onEvent: (ProfileEvent) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFFFFDF7)) {
        Box(Modifier.fillMaxSize()) {
            Image(painterResource(R.drawable.profile_heritage_header), null, Modifier.fillMaxWidth().height(192.dp))
            Column(
                modifier = Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            ) {
                Text("Profile", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = ProfileNavy)
                Text("Your learning journey, in one place", style = MaterialTheme.typography.bodyMedium, color = ProfileMuted)
                Spacer(Modifier.height(62.dp))
                ProfileIdentityCard(state.displayName)
                Spacer(Modifier.height(18.dp))
                Text("Your space", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = ProfileNavy)
                Spacer(Modifier.height(10.dp))
                ProfileMenuCard("My languages", "Choose or change your language path", Icons.Default.Language, Color(0xFFE9F8EE), ProfileGreen) { onEvent(ProfileEvent.OpenLanguages) }
                Spacer(Modifier.height(10.dp))
                ProfileMenuCard("Learning progress", "See your streak, XP, and study time", Icons.Default.BarChart, Color(0xFFFFF3D9), Color(0xFFD88712)) { onEvent(ProfileEvent.OpenStatistics) }
                Spacer(Modifier.height(10.dp))
                ProfileMenuCard("Achievements", "Celebrate every milestone", Icons.Default.EmojiEvents, Color(0xFFE8F2FF), Color(0xFF2E73C8)) { onEvent(ProfileEvent.OpenAchievements) }
                Spacer(Modifier.height(10.dp))
                ProfileMenuCard("Account & cloud backup", "Save your lessons across devices", Icons.Default.CloudDone, Color(0xFFE6F7EF), ProfileGreen) { onEvent(ProfileEvent.OpenAccount) }
                Spacer(Modifier.height(10.dp))
                ProfileMenuCard("Settings", "Accessibility, sound, and preferences", Icons.Default.Settings, Color(0xFFF0ECFA), Color(0xFF6E52B4)) { onEvent(ProfileEvent.OpenSettings) }
                Spacer(Modifier.height(10.dp))
                ProfileMenuCard("Help & support", "Get help with your learning", Icons.AutoMirrored.Filled.HelpOutline, Color(0xFFFFECE7), Color(0xFFC05235)) { onEvent(ProfileEvent.OpenHelpSupport) }
                Spacer(Modifier.height(10.dp))
                RestartSetupRow { onEvent(ProfileEvent.SignOut) }
                Spacer(Modifier.height(24.dp))
                Text("VaaniVerse4U • Learn local. Grow together.", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.labelMedium, color = ProfileMuted)
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun ProfileIdentityCard(displayName: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFFFC)), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = Color(0xFFFFE9B9)) {
                    Icon(Icons.Default.Person, "Profile avatar", Modifier.padding(14.dp), tint = ProfileGreen)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = ProfileNavy)
                    Spacer(Modifier.height(4.dp))
                    Surface(shape = RoundedCornerShape(99.dp), color = Color(0xFFE5F7EA)) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDone, null, Modifier.size(15.dp), ProfileGreen)
                            Spacer(Modifier.width(5.dp))
                            Text("Guest • backup ready", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = ProfileGreen)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = Color(0xFFF7FAF4)) {
                Row(modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ProfileMetric(Icons.Default.LocalFireDepartment, "0", "day streak", Color(0xFFE78512))
                    ProfileMetric(Icons.Default.Star, "0", "XP", Color(0xFFE2A323))
                    ProfileMetric(Icons.Default.MenuBook, "0", "lessons", ProfileGreen)
                }
            }
        }
    }
}

@Composable
private fun RowScope.ProfileMetric(icon: ImageVector, value: String, label: String, tint: Color) {
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(23.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = ProfileNavy)
        Text(label, style = MaterialTheme.typography.labelSmall, color = ProfileMuted)
    }
}

@Composable
private fun ProfileMenuCard(title: String, subtitle: String, icon: ImageVector, tintSurface: Color, tint: Color, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = tintSurface)) {
        Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Color(0xCCFFFFFF)) {
                Icon(icon, null, Modifier.padding(12.dp), tint)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = ProfileNavy)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = ProfileMuted)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Open $title", tint = tint)
        }
    }
}

@Composable
private fun RestartSetupRow(onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFB9382A))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Restart setup", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFB9382A))
            Text("Choose a new language path", style = MaterialTheme.typography.bodySmall, color = ProfileMuted)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Restart setup", tint = Color(0xFFB9382A))
    }
}
