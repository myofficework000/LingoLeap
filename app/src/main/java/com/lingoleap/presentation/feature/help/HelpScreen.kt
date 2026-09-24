package com.lingoleap.presentation.feature.help

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HelpRoute(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Help & support", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        HelpItem("How does offline learning work?", "Lessons, quizzes, daily challenges, and progress are stored on this device. You can learn without an internet connection.")
        HelpItem("Why is pronunciation unavailable?", "Pronunciation uses your device text-to-speech engine. Install the required language voice in Android settings, then try again.")
        HelpItem("How do I change language?", "Open Profile, choose My Languages, and select a new source and target language.")
        HelpItem("How do I start again?", "Use Settings to reset learning progress, or Profile → Restart setup to return to onboarding.")
        Spacer(Modifier.weight(1f))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
private fun HelpItem(title: String, body: String) {
    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
