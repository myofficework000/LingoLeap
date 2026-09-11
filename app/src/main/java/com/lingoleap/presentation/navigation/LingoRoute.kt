package com.lingoleap.presentation.navigation

sealed class LingoRoute(val path: String) {
    data object Onboarding : LingoRoute("onboarding")
    data object LanguagePicker : LingoRoute("language_picker")
    data object Home : LingoRoute("home")
    data object Lesson : LingoRoute("lesson/{lessonId}")
    data object Quiz : LingoRoute("quiz/{quizId}")
    data object Practice : LingoRoute("practice")
    data object Progress : LingoRoute("progress")
    data object Profile : LingoRoute("profile")
}
