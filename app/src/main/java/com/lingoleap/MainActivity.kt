package com.lingoleap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lingoleap.presentation.feature.home.HomeScreen
import com.lingoleap.presentation.feature.language.LanguagePickerRoute
import com.lingoleap.presentation.feature.language.LanguagePickerScreen
import com.lingoleap.presentation.feature.lesson.LessonScreen
import com.lingoleap.presentation.feature.onboarding.OnboardingRoute
import com.lingoleap.presentation.feature.onboarding.OnboardingScreen
import com.lingoleap.presentation.feature.practice.PracticeRoute
import com.lingoleap.presentation.feature.practice.PracticeScreen
import com.lingoleap.presentation.feature.profile.ProfileEffect
import com.lingoleap.presentation.feature.profile.ProfileScreen
import com.lingoleap.presentation.feature.profile.ProfileViewModel
import com.lingoleap.presentation.feature.progress.ProgressEffect
import com.lingoleap.presentation.feature.progress.ProgressScreen
import com.lingoleap.presentation.feature.progress.ProgressViewModel
import com.lingoleap.presentation.feature.quiz.QuizRoute
import com.lingoleap.presentation.feature.quiz.QuizScreen
import com.lingoleap.presentation.navigation.LingoRoute
import com.lingoleap.presentation.theme.LingoLeapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) = super.onCreate(savedInstanceState).also {
        setContent { LingoLeapTheme { LingoLeapApp() } }
    }
}

@Composable
private fun LingoLeapApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = LingoRoute.Onboarding.path) {
        composable(LingoRoute.Onboarding.path) {
            OnboardingRoute(onFinished = {
                navController.navigate(LingoRoute.LanguagePicker.path)
            })
        }
        composable(LingoRoute.LanguagePicker.path) {
            LanguagePickerRoute(
                onConfirmed = {
                    navController.navigate(LingoRoute.Home.path)
                }
            )
        }
        composable(LingoRoute.Home.path) { HomeScreen() }
        composable(LingoRoute.Lesson.path) { LessonScreen(onEvent = {}) }
        composable(LingoRoute.Quiz.path) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            QuizRoute(
                lessonId = lessonId,
                onNext = {
                    navController.navigate(LingoRoute.Practice.path)
                }
            )
        }
        composable(LingoRoute.Practice.path) { backStackEntry ->

            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            PracticeRoute(
                lessonId = lessonId
            )
        }
        composable(LingoRoute.Progress.path) {
            val viewModel: ProgressViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                viewModel.effect.collectLatest { effect ->

                    when (effect) {

                        ProfileEffect.NavigateToLanguages -> {
                            // Navigation
                        }

                        ProfileEffect.NavigateToStatistics -> {
                            // Navigation
                        }

                        ProfileEffect.NavigateToAchievements -> {
                            // Navigation
                        }

                        ProfileEffect.NavigateToSettings -> {
                            // Navigation
                        }

                        ProfileEffect.NavigateToHelpSupport -> {
                            // Navigation
                        }

                        ProfileEffect.SignOut -> {
                            // Sign-out
                        }

                        else -> {}
                    }
                }
            }
            ProgressScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        }

        composable(LingoRoute.Profile.path) {

            val viewModel: ProfileViewModel = hiltViewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()

            ProfileScreen(
                state = state, onEvent = viewModel::onEvent) }
    }
}
