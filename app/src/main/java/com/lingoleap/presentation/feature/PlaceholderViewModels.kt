package com.lingoleap.presentation.feature

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Intentionally empty MVI hosts. Students inject their feature use cases and add reducers. */
@HiltViewModel class OnboardingViewModel @Inject constructor() : ViewModel()
@HiltViewModel class LanguagePickerViewModel @Inject constructor() : ViewModel()
@HiltViewModel class HomeViewModel @Inject constructor() : ViewModel()
@HiltViewModel class LessonViewModel @Inject constructor() : ViewModel()
@HiltViewModel class QuizViewModel @Inject constructor() : ViewModel()
@HiltViewModel class PracticeViewModel @Inject constructor() : ViewModel()
@HiltViewModel class ProgressViewModel @Inject constructor() : ViewModel()
@HiltViewModel class ProfileViewModel @Inject constructor() : ViewModel()
