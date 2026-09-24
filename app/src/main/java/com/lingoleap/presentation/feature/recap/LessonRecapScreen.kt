package com.lingoleap.presentation.feature.recap

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonRecapViewModel @Inject constructor(private val getCourses: GetCoursesUseCase) : ViewModel() {
    private val _lesson = MutableStateFlow<Lesson?>(null); val lesson = _lesson.asStateFlow()
    fun load(id: String) = viewModelScope.launch { _lesson.value = getCourses().flatMap { it.lessons }.firstOrNull { it.id == id } }
}
@Composable
fun LessonRecapRoute(lessonId: String, onQuiz: () -> Unit, onHome: () -> Unit, viewModel: LessonRecapViewModel = hiltViewModel()) {
    val lesson by viewModel.lesson.collectAsStateWithLifecycle(); LaunchedEffect(lessonId) { viewModel.load(lessonId) }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Lesson complete!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Review the words you just learned, then check your memory.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(18.dp))
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(lesson?.words.orEmpty(), key = { it.id }) { word -> Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) { Column(Modifier.weight(1f)) { Text(word.sourceText, fontWeight = FontWeight.Bold); word.transliteration?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) } }; Text(word.targetText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) } } } }
        Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth()) { Text("Take the quick quiz") }
        TextButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) { Text("Finish for now") }
    }
}
