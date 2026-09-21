package com.lingoleap.presentation.feature.learningpath

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.model.LearningPathNode
import com.lingoleap.domain.model.LearningPathNodeState
import com.lingoleap.domain.usecase.GetCoursesUseCase
import com.lingoleap.domain.usecase.ObserveLearnerProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearningPathState(
    val nodes: List<LearningPathNode> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class LearningPathViewModel @Inject constructor(
    private val getCourses: GetCoursesUseCase,
    private val observeProgress: ObserveLearnerProgressUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        LearningPathState()
    )

    val state = _state.asStateFlow()

    init {
        observeLearningPath()
    }

    private fun observeLearningPath() {

        viewModelScope.launch {

            val courses = getCourses()

            observeProgress().collect { progress ->

                val lessons = courses
                    .firstOrNull { course ->
                        course.id == progress.activeCourseId
                    }
                    ?.lessons
                    .orEmpty()

                val nodes = lessons.map { lesson ->

                    val nodeState = when {

                        lesson.id in progress.completedLessonIds -> {
                            LearningPathNodeState.COMPLETED
                        }

                        lessons
                            .take(lesson.order - 1)
                            .all { previousLesson ->
                                previousLesson.id in progress.completedLessonIds
                            } -> {
                            LearningPathNodeState.CURRENT
                        }

                        else -> {
                            LearningPathNodeState.LOCKED
                        }
                    }

                    LearningPathNode(
                        lesson = lesson,
                        state = nodeState
                    )
                }

                _state.update { currentState ->

                    currentState.copy(
                        nodes = nodes,
                        isLoading = false
                    )
                }
            }
        }
    }
}