package com.lingoleap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lingoleap.presentation.feature.home.HomeScreen
import com.lingoleap.presentation.feature.language.LanguagePickerRoute
import com.lingoleap.presentation.feature.lesson.LessonEvent
import com.lingoleap.presentation.feature.lesson.LessonScreen
import com.lingoleap.presentation.feature.lesson.LessonsListScreen
import com.lingoleap.presentation.feature.onboarding.OnboardingRoute
import com.lingoleap.presentation.feature.practice.PracticeRoute
import com.lingoleap.presentation.feature.profile.ProfileEffect
import com.lingoleap.presentation.feature.profile.ProfileScreen
import com.lingoleap.presentation.feature.profile.ProfileViewModel
import com.lingoleap.presentation.feature.progress.AchievementsScreen
import com.lingoleap.presentation.feature.progress.ProgressEffect
import com.lingoleap.presentation.feature.progress.ProgressScreen
import com.lingoleap.presentation.feature.progress.ProgressViewModel
import com.lingoleap.presentation.feature.quiz.QuizRoute
import com.lingoleap.presentation.navigation.LingoBottomBar
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
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in setOf(
        LingoRoute.Home.path, LingoRoute.Lessons.path, LingoRoute.Practice.path, LingoRoute.Profile.path,
    )

    Scaffold(bottomBar = { if (showBottomBar) LingoBottomBar(navController) }) { padding ->
        NavHost(navController, LingoRoute.Onboarding.path, androidx.compose.ui.Modifier.padding(padding)) {
            composable(LingoRoute.Onboarding.path) {
                OnboardingRoute(onFinished = {
                    navController.navigate(LingoRoute.LanguagePicker.path) { popUpTo(LingoRoute.Onboarding.path) { inclusive = true } }
                })
            }
            composable(LingoRoute.LanguagePicker.path) {
                LanguagePickerRoute(onConfirmed = {
                    navController.navigate(LingoRoute.Home.path) { popUpTo(LingoRoute.LanguagePicker.path) { inclusive = true } }
                })
            }
            composable(LingoRoute.Home.path) {
                HomeScreen(
                    onContinueLearning = { navController.navigate(LingoRoute.Lessons.path) },
                    onPractice = { navController.navigate(LingoRoute.Practice.create("hi-basics")) },
                    onProfile = { navController.navigate(LingoRoute.Profile.path) },
                )
            }
            composable(LingoRoute.Lessons.path) {
                LessonsListScreen(
                    onBack = { navController.popBackStack() },
                    onLessonClick = { lessonId -> navController.navigate(LingoRoute.Lesson.create(lessonId)) },
                )
            }
            composable(LingoRoute.Lesson.path) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                LessonScreen(onEvent = { event ->
                    if (event == LessonEvent.Finish) navController.navigate(LingoRoute.Quiz.create(lessonId))
                })
            }
            composable(LingoRoute.Quiz.path) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                QuizRoute(lessonId = lessonId, onNext = { navController.navigate(LingoRoute.Practice.create(lessonId)) })
            }
            composable(LingoRoute.Practice.path) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                PracticeRoute(lessonId = lessonId)
            }
            composable(LingoRoute.Progress.path) {
                val viewModel: ProgressViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                LaunchedEffect(viewModel) {
                    viewModel.effect.collectLatest { if (it is ProgressEffect.NavigateToAchievements) navController.navigate(LingoRoute.Achievements.path) }
                }
                ProgressScreen(state = state, onEvent = viewModel::onEvent)
            }
            composable(LingoRoute.Profile.path) {
                val viewModel: ProfileViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                LaunchedEffect(viewModel) {
                    viewModel.effect.collectLatest { effect ->
                        when (effect) {
                            ProfileEffect.NavigateToLanguages -> navController.navigate(LingoRoute.LanguagePicker.path)
                            ProfileEffect.NavigateToStatistics -> navController.navigate(LingoRoute.Progress.path)
                            ProfileEffect.NavigateToAchievements -> navController.navigate(LingoRoute.Achievements.path)
                            ProfileEffect.SignOut -> navController.navigate(LingoRoute.Onboarding.path) { popUpTo(0) { inclusive = true } }
                            else -> Unit
                        }
                    }
                }
                ProfileScreen(state = state, onEvent = viewModel::onEvent)
            }
            composable(LingoRoute.Achievements.path) { AchievementsScreen(onEvent = { navController.popBackStack() }) }
        }
    }
}
