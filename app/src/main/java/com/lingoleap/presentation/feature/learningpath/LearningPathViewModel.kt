package com.lingoleap.presentation.feature.learningpath

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.model.LearningPathNode
import com.lingoleap.domain.model.LearningPathNodeState
import com.lingoleap.domain.usecase.GetCoursesUseCase
import com.lingoleap.domain.usecase.GetLearnerProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearningPathState(val nodes: List<LearningPathNode> = emptyList(), val isLoading: Boolean = true)

@HiltViewModel
class LearningPathViewModel @Inject constructor(
    private val getCourses: GetCoursesUseCase,
    private val getProgress: GetLearnerProgressUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(LearningPathState())
    val state = _state.asStateFlow()
    init { viewModelScope.launch {
        val progress = getProgress()
        val lessons = getCourses().firstOrNull { it.id == progress.activeCourseId }?.lessons.orEmpty()
        _state.value = LearningPathState(lessons.map { lesson ->
            val state = when {
                lesson.id in progress.completedLessonIds -> LearningPathNodeState.COMPLETED
                lessons.take(lesson.order - 1).all { it.id in progress.completedLessonIds } -> LearningPathNodeState.CURRENT
                else -> LearningPathNodeState.LOCKED
            }
            LearningPathNode(lesson, state)
        }, isLoading = false)
    } }
}
