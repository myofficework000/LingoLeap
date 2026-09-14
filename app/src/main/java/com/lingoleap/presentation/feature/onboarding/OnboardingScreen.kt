package com.lingoleap.presentation.feature.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.R

data class OnboardingState(
    val pageIndex: Int = 0,
    val isLoading: Boolean = false
) : UiState

sealed interface OnboardingEvent : UiEvent {
    data object Continue : OnboardingEvent
    data object Skip : OnboardingEvent
}

data class OnboardingPageUi(
    val title: String,
    val subtitle: String,

)

private val onboardingPages = listOf(
    OnboardingPageUi(
        title = "Welcome to LingoLeap",
        subtitle = "Learn local languages in a fun and simple way."
    ),
    OnboardingPageUi(
        title = "Learn with Quizzes and Games",
        subtitle = "Build real skills using lessons, practice and challenges."
    ),
    OnboardingPageUi(
        title = "Track Your Progress",
        subtitle = "Stay consistent and grow every day with progress tracking."
    )
)

@Composable
fun OnboardingRoute(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OnboardingScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onFinished = onFinished
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onEvent: (OnboardingEvent) -> Unit,
    onFinished: () -> Unit
) {
    val currentPage = onboardingPages[state.pageIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onFinished
            ) {
                Text("Skip")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

//        Image(
//            painter = painterResource(id = currentPage.imageRes),
//            contentDescription = null,
//            modifier = Modifier.size(220.dp)
//        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = currentPage.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = currentPage.subtitle,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(onboardingPages.size) { index ->
                Box(
                    modifier = Modifier
                        .size(
                            width = if (index == state.pageIndex) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .clip(CircleShape)
                        .background(
                            if (index == state.pageIndex) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (state.pageIndex < onboardingPages.lastIndex) {
                    onEvent(OnboardingEvent.Continue)
                } else {
                    onFinished()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text(
                text = if (state.pageIndex == onboardingPages.lastIndex) {
                    "Get Started"
                } else {
                    "Continue"
                },
                fontSize = 16.sp
            )
        }
    }
}
