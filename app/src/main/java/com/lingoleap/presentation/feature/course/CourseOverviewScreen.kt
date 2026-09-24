package com.lingoleap.presentation.feature.course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.model.Course
import com.lingoleap.domain.usecase.GetCoursesUseCase
import com.lingoleap.domain.usecase.GetLearnerProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseOverviewViewModel @Inject constructor(private val courses: GetCoursesUseCase, private val progress: GetLearnerProgressUseCase) : ViewModel() { private val _course = MutableStateFlow<Course?>(null); val course = _course.asStateFlow(); init { viewModelScope.launch { val p = progress(); _course.value = courses().firstOrNull { it.id == p.activeCourseId } } } }
@Composable
fun CourseOverviewRoute(onOpenLessons: () -> Unit, onBack: () -> Unit, viewModel: CourseOverviewViewModel = hiltViewModel()) { val course by viewModel.course.collectAsStateWithLifecycle(); Column(Modifier.fillMaxSize().padding(20.dp)) { Text(course?.title ?: "Your course", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("Every unit works offline: learn vocabulary, take a quiz, then practise it.", color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(18.dp)); LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(course?.lessons.orEmpty(), key = { it.id }) { lesson -> Card { Column(Modifier.padding(16.dp)) { Text("Unit ${lesson.order}", color = MaterialTheme.colorScheme.primary); Text(lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("${lesson.words.size} words · lesson + quiz + practice") } } } }; Button(onClick = onOpenLessons, modifier = Modifier.fillMaxWidth()) { Text("Open learning path") }; TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") } } }
