package com.lingoleap.presentation.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.usecase.GetCoursesUseCase
import com.lingoleap.domain.usecase.GetLearnerProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = true,
    val course: Course? = null,
    val progress: LearnerProgress? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val getLearnerProgressUseCase: GetLearnerProgressUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    init {
        loadHomeData()
    }

    private fun loadHomeData() {

        viewModelScope.launch {

            try {

                val (courses, progress) = coroutineScope {
                    val courses = async { getCoursesUseCase() }
                    val progress = async { getLearnerProgressUseCase() }
                    courses.await() to progress.await()
                }

                val activeCourse = courses.firstOrNull { course ->
                    course.id == progress.activeCourseId
                }

                _state.value = HomeState(
                    isLoading = false,
                    course = activeCourse,
                    progress = progress
                )

            } catch (exception: Exception) {

                _state.value = HomeState(
                    isLoading = false,
                    error = exception.message
                )
            }
        }
    }
}
