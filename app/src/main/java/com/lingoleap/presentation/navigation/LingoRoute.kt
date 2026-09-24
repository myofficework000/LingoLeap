package com.lingoleap.presentation.navigation

sealed class LingoRoute(val path: String) {
    data object Splash : LingoRoute("splash")
    data object Onboarding : LingoRoute("onboarding")
    data object LanguagePicker : LingoRoute("language_picker")
    data object GoalSetup : LingoRoute("goal_setup")
    data object Home : LingoRoute("home")
    data object Lessons : LingoRoute("lessons")
    data object Lesson : LingoRoute("lesson/{lessonId}") { fun create(lessonId: String) = "lesson/$lessonId" }
    data object Quiz : LingoRoute("quiz/{lessonId}") { fun create(lessonId: String) = "quiz/$lessonId" }
    data object Practice : LingoRoute("practice/{lessonId}") { fun create(lessonId: String) = "practice/$lessonId" }
    data object PracticeHub : LingoRoute("practice_hub")
    data object DailyChallenge : LingoRoute("daily_challenge")
    data object LearningPath : LingoRoute("learning_path")
    data object Progress : LingoRoute("progress")
    data object Profile : LingoRoute("profile")
    data object Achievements : LingoRoute("achievements")
    data object CourseOverview : LingoRoute("course_overview")
    data object LessonRecap : LingoRoute("lesson_recap/{lessonId}") { fun create(lessonId: String) = "lesson_recap/$lessonId" }
    data object Review : LingoRoute("review")
    data object Settings : LingoRoute("settings")

}
