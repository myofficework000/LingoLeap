package com.lingoleap.presentation.feature.practice

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
import com.lingoleap.domain.model.Lesson
import com.lingoleap.domain.usecase.GetCoursesUseCase
import com.lingoleap.domain.usecase.GetLearnerProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PracticeHubViewModel @Inject constructor(private val courses: GetCoursesUseCase, private val progress: GetLearnerProgressUseCase) : ViewModel() { private val _lessons = MutableStateFlow<List<Lesson>>(emptyList()); val lessons = _lessons.asStateFlow(); init { viewModelScope.launch { val current = progress(); _lessons.value = courses().firstOrNull { it.id == current.activeCourseId }?.lessons.orEmpty() } } }
@Composable
fun PracticeHubRoute(onStart: (String) -> Unit, viewModel: PracticeHubViewModel = hiltViewModel()) { val lessons by viewModel.lessons.collectAsStateWithLifecycle(); Column(Modifier.fillMaxSize().padding(20.dp)) { Text("Practice", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("Choose a completed topic to practise vocabulary, listening, and fill-in-the-blank.", color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.height(16.dp)); LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) { items(lessons, key = { it.id }) { lesson -> Card(onClick = { onStart(lesson.id) }, modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text("${lesson.words.size} words · tap to practise") } } } } } }
