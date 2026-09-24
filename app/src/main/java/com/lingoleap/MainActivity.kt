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
import com.lingoleap.presentation.feature.home.HomeEvent
import com.lingoleap.presentation.feature.home.HomeScreen
import com.lingoleap.presentation.feature.challenge.DailyChallengeRoute
import com.lingoleap.presentation.feature.language.LanguagePickerRoute
import com.lingoleap.presentation.feature.learningpath.LearningPathScreen
import com.lingoleap.presentation.feature.lesson.LessonRoute
import com.lingoleap.presentation.feature.lesson.LessonsListEvent
import com.lingoleap.presentation.feature.lesson.LessonsListScreen
import com.lingoleap.presentation.feature.onboarding.OnboardingRoute
import com.lingoleap.presentation.feature.practice.PracticeRoute
import com.lingoleap.presentation.feature.splash.SplashRoute
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
        NavHost(navController, LingoRoute.Splash.path, androidx.compose.ui.Modifier.padding(padding)) {
            composable(LingoRoute.Splash.path) {
                SplashRoute(onNavigate = { route -> navController.navigate(route) { popUpTo(LingoRoute.Splash.path) { inclusive = true } } })
            }
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
                    onEvent = { event ->

                        when (event) {

                            HomeEvent.ContinueLearning -> {

                                navController.navigate(
                                    LingoRoute.Lessons.path
                                )
                            }

                            HomeEvent.Practice -> {

                                navController.navigate(
                                    LingoRoute.Practice.create(
                                        lessonId = "en-hi-greetings"
                                    )
                                )
                            }

                            HomeEvent.Profile -> {

                                navController.navigate(
                                    LingoRoute.Profile.path
                                )
                            }

                            HomeEvent.DailyChallenge -> {
                                navController.navigate(LingoRoute.DailyChallenge.path)
                            }

                            HomeEvent.LearningPath -> {
                                navController.navigate(LingoRoute.LearningPath.path)
                            }
                        }
                    }
                )
            }
            composable(LingoRoute.Lessons.path) {
                LessonsListScreen(
                    onEvent = { event ->

                        when (event) {

                            LessonsListEvent.Back -> {
                                navController.popBackStack()
                            }

                            is LessonsListEvent.LessonClicked -> {

                                navController.navigate(
                                    LingoRoute.Lesson.create(
                                        event.lessonId
                                    )
                                )
                            }
                        }
                    }
                )
            }
            composable(LingoRoute.Lesson.path) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                LessonRoute(lessonId = lessonId, onCompleted = { navController.navigate(LingoRoute.Quiz.create(lessonId)) })
            }
            composable(LingoRoute.Quiz.path) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                QuizRoute(lessonId = lessonId, onNext = { navController.navigate(LingoRoute.Practice.create(lessonId)) })
            }
            composable(
                route = LingoRoute.Practice.path
            ) { backStackEntry ->

                val lessonId =
                    backStackEntry.arguments
                        ?.getString("lessonId")
                        ?: return@composable

                PracticeRoute(
                    lessonId = lessonId,
                    onFinished = {
                        navController.navigate(
                            LingoRoute.Home.path
                        ) {
                            popUpTo(
                                LingoRoute.Practice.path
                            ) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable(LingoRoute.Progress.path) {
                val viewModel: ProgressViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                LaunchedEffect(viewModel) {
                    viewModel.effect.collectLatest { effect ->

                        when (effect) {

                            ProgressEffect.NavigateToAchievements -> {

                                navController.navigate(
                                    LingoRoute.Achievements.path
                                )
                            }

                            ProgressEffect.NavigateToLearningPath -> {

                                navController.navigate(
                                    LingoRoute.LearningPath.path
                                )
                            }
                        }
                    }
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

            composable(LingoRoute.LearningPath.path) {

                LearningPathScreen(
                    onLessonClick = { lessonId ->

                        navController.navigate(
                            LingoRoute.Lesson.create(lessonId)
                        )
                    }
                )
            }

            composable(LingoRoute.Achievements.path) { AchievementsScreen(onEvent = { navController.popBackStack() }) }
            composable(LingoRoute.DailyChallenge.path) {
                DailyChallengeRoute(
                    onBack = { navController.popBackStack() },
                    onFinished = { navController.navigate(LingoRoute.Home.path) { popUpTo(LingoRoute.Home.path) { inclusive = false } } },
                )
            }
        }
    }
}

