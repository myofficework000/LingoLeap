package com.lingoleap.presentation.feature.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.usecase.GetCoursesUseCase
import com.lingoleap.domain.usecase.GetLearnerProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class LessonsListState(
    val course: Course? = null,
    val progress: LearnerProgress? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class LessonsListViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val getLearnerProgressUseCase: GetLearnerProgressUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LessonsListState())
    val state: StateFlow<LessonsListState> = _state.asStateFlow()

    init {
        loadLessons()
    }

    private fun loadLessons() {

        viewModelScope.launch {

            try {

                val (courses, progress) = coroutineScope {
                    val courses = async { getCoursesUseCase() }
                    val progress = async { getLearnerProgressUseCase() }
                    courses.await() to progress.await()
                }

                val activeCourse = courses.firstOrNull {
                    it.id == progress.activeCourseId
                }

                _state.value = LessonsListState(
                    course = activeCourse,
                    progress = progress,
                    isLoading = false
                )

            } catch (e: Exception) {

                _state.value = LessonsListState(
                    isLoading = false,
                    error = e.message ?: "Something went wrong"
                )
            }
        }
    }
}
