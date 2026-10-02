package com.code4galaxy.vaaniverse4u

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
import com.code4galaxy.vaaniverse4u.presentation.feature.home.HomeEvent
import com.code4galaxy.vaaniverse4u.presentation.feature.home.HomeScreen
import com.code4galaxy.vaaniverse4u.presentation.feature.challenge.DailyChallengeRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.goal.GoalSetupRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.course.CourseOverviewRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.recap.LessonRecapRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.review.ReviewRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.settings.SettingsRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.help.HelpRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.language.LanguagePickerRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.learningpath.LearningPathScreen
import com.code4galaxy.vaaniverse4u.presentation.feature.lesson.LessonRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.lesson.LessonsListEvent
import com.code4galaxy.vaaniverse4u.presentation.feature.lesson.LessonsListScreen
import com.code4galaxy.vaaniverse4u.presentation.feature.onboarding.OnboardingRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.practice.PracticeRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.splash.SplashRoute
import com.code4galaxy.vaaniverse4u.presentation.feature.profile.ProfileEffect
import com.code4galaxy.vaaniverse4u.presentation.feature.profile.ProfileScreen
import com.code4galaxy.vaaniverse4u.presentation.feature.profile.ProfileViewModel
import com.code4galaxy.vaaniverse4u.presentation.feature.progress.AchievementsScreen
import com.code4galaxy.vaaniverse4u.presentation.feature.progress.ProgressEffect
import com.code4galaxy.vaaniverse4u.presentation.feature.progress.ProgressScreen
import com.code4galaxy.vaaniverse4u.presentation.feature.progress.ProgressViewModel
import com.code4galaxy.vaaniverse4u.presentation.feature.quiz.QuizRoute
import com.code4galaxy.vaaniverse4u.presentation.navigation.LingoBottomBar
import com.code4galaxy.vaaniverse4u.presentation.navigation.LingoRoute
import com.code4galaxy.vaaniverse4u.presentation.accessibility.AppAccessibilityViewModel
import com.code4galaxy.vaaniverse4u.presentation.theme.VaaniVerse4UTheme
import com.code4galaxy.vaaniverse4u.presentation.theme.VaaniVerse4UTextScale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) = super.onCreate(savedInstanceState).also {
        setContent { VaaniVerse4UApp() }
    }
}

@Composable
private fun VaaniVerse4UApp() {
    val accessibilityViewModel: AppAccessibilityViewModel = hiltViewModel()
    val accessibilityState by accessibilityViewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = currentRoute in setOf(
        LingoRoute.Home.path, LingoRoute.Lessons.path, LingoRoute.PracticeHub.path, LingoRoute.Profile.path,
    )

    VaaniVerse4UTheme(highContrast = accessibilityState.highContrastEnabled) {
        VaaniVerse4UTextScale(scale = accessibilityState.textScale) {
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
                    navController.navigate(LingoRoute.GoalSetup.path) { popUpTo(LingoRoute.LanguagePicker.path) { inclusive = true } }
                })
            }
            composable(LingoRoute.GoalSetup.path) { GoalSetupRoute(onFinished = { navController.navigate(LingoRoute.Home.path) { popUpTo(LingoRoute.GoalSetup.path) { inclusive = true } } }) }
            composable(LingoRoute.Home.path) {
                HomeScreen(
                    onEvent = { event ->

                        when (event) {

                            HomeEvent.ContinueLearning -> {

                                navController.navigate(LingoRoute.CourseOverview.path)
                            }

                            HomeEvent.Practice -> {
                                navController.navigate(LingoRoute.PracticeHub.path)
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

                            HomeEvent.Review -> {
                                navController.navigate(LingoRoute.Review.path)
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
                LessonRoute(
                    lessonId = lessonId,
                    onCompleted = { navController.navigate(LingoRoute.LessonRecap.create(lessonId)) },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(LingoRoute.LessonRecap.path) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                LessonRecapRoute(lessonId = lessonId, onQuiz = { navController.navigate(LingoRoute.Quiz.create(lessonId)) }, onHome = { navController.navigate(LingoRoute.Home.path) { popUpTo(LingoRoute.Home.path) { inclusive = false } } })
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
            composable(LingoRoute.PracticeHub.path) {
                com.code4galaxy.vaaniverse4u.presentation.feature.practice.PracticeHubRoute(
                    onStart = { lessonId -> navController.navigate(LingoRoute.Practice.create(lessonId)) }
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
                            ProfileEffect.NavigateToSettings -> navController.navigate(LingoRoute.Settings.path)
                            ProfileEffect.NavigateToHelpSupport -> navController.navigate(LingoRoute.Help.path)
                            ProfileEffect.SignOut -> navController.navigate(LingoRoute.Onboarding.path) { popUpTo(0) { inclusive = true } }
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
            composable(LingoRoute.CourseOverview.path) { CourseOverviewRoute(onOpenLessons = { navController.navigate(LingoRoute.Lessons.path) }, onBack = { navController.popBackStack() }) }
            composable(LingoRoute.Review.path) { ReviewRoute(onBack = { navController.popBackStack() }) }
            composable(LingoRoute.Settings.path) { SettingsRoute(onBack = { navController.popBackStack() }) }
            composable(LingoRoute.Help.path) { HelpRoute(onBack = { navController.popBackStack() }) }
            composable(LingoRoute.DailyChallenge.path) {
                DailyChallengeRoute(
                    onBack = { navController.popBackStack() },
                    onFinished = { navController.navigate(LingoRoute.Home.path) { popUpTo(LingoRoute.Home.path) { inclusive = false } } },
                )
            }
                }
            }
        }
    }
}
