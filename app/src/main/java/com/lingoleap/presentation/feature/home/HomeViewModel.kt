package com.lingoleap.presentation.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.usecase.GetCoursesUseCase
import com.lingoleap.domain.usecase.GetLearnerProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = true,
    val course: Course? = null,
    val progress: LearnerProgress? = null,
    val error: String? = null
) : UiState

sealed interface HomeEvent : UiEvent {

    data object ContinueLearning : HomeEvent

    data object Practice : HomeEvent

    data object Profile : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val getLearnerProgressUseCase: GetLearnerProgressUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow(HomeState())

    val state =
        _state.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {

        viewModelScope.launch {

            _state.value =
                _state.value.copy(
                    isLoading = true,
                    error = null
                )

            try {

                val (courses, progress) =
                    coroutineScope {

                        val courses =
                            async {
                                getCoursesUseCase()
                            }

                        val progress =
                            async {
                                getLearnerProgressUseCase()
                            }

                        courses.await() to
                                progress.await()
                    }

                val activeCourse =
                    courses.firstOrNull {
                        it.id ==
                                progress.activeCourseId
                    }

                _state.value =
                    HomeState(
                        isLoading = false,
                        course = activeCourse,
                        progress = progress
                    )

            } catch (exception: Exception) {

                _state.value =
                    HomeState(
                        isLoading = false,
                        error =
                            exception.message
                                ?: "Unable to load home"
                    )
            }
        }
    }

    fun onEvent(event: HomeEvent) {

        when (event) {

            HomeEvent.ContinueLearning -> {

            }

            HomeEvent.Practice -> {

            }

            HomeEvent.Profile -> {

            }
        }
    }
}