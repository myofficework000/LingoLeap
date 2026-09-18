package com.lingoleap.presentation.feature.learningpath

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.LearningPathNode
import com.lingoleap.domain.model.LearningPathNodeState
import com.lingoleap.domain.usecase.GetCoursesUseCase
import com.lingoleap.domain.usecase.ObserveLearnerProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearningPathState(
    val courseTitle: String = "Learning path",
    val nodes: List<LearningPathNode> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
) : UiState

sealed interface LearningPathEvent : UiEvent {
    data object Back : LearningPathEvent
    data class SelectLesson(val lessonId: String) : LearningPathEvent
}

sealed interface LearningPathEffect {
    data object NavigateBack : LearningPathEffect
    data class NavigateToLesson(val lessonId: String) : LearningPathEffect
}

@HiltViewModel
class LearningPathViewModel @Inject constructor(
    private val getCourses: GetCoursesUseCase,
    private val observeProgress: ObserveLearnerProgressUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(LearningPathState())
    val state = _state.asStateFlow()
    private val _effect = Channel<LearningPathEffect>(Channel.BUFFERED)
    val effect: Flow<LearningPathEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            runCatching { getCourses() }
                .onSuccess { courses -> observePath(courses) }
                .onFailure { error -> _state.value = LearningPathState(isLoading = false, error = error.message ?: "Unable to load your learning path") }
        }
    }

    fun onEvent(event: LearningPathEvent) {
        when (event) {
            LearningPathEvent.Back -> viewModelScope.launch { _effect.send(LearningPathEffect.NavigateBack) }
            is LearningPathEvent.SelectLesson -> viewModelScope.launch { _effect.send(LearningPathEffect.NavigateToLesson(event.lessonId)) }
        }
    }

    private suspend fun observePath(courses: List<com.lingoleap.domain.model.Course>) {
        observeProgress().collect { progress ->
            val course = courses.firstOrNull { it.id == progress.activeCourseId }
            val lessons = course?.lessons.orEmpty()
            _state.value = LearningPathState(
                courseTitle = course?.title ?: "Learning path",
                nodes = lessons.map { lesson ->
                    val status = when {
                        lesson.id in progress.completedLessonIds -> LearningPathNodeState.COMPLETED
                        lessons.take(lesson.order - 1).all { it.id in progress.completedLessonIds } -> LearningPathNodeState.CURRENT
                        else -> LearningPathNodeState.LOCKED
                    }
                    LearningPathNode(lesson, status)
                },
                isLoading = false,
            )
        }
    }
}
