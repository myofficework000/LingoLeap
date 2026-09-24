package com.lingoleap.presentation.feature.review

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
import com.lingoleap.domain.model.VocabularyWord
import com.lingoleap.domain.usecase.GetCoursesUseCase
import com.lingoleap.domain.usecase.ObserveLearnerProgressUseCase
import com.lingoleap.domain.usecase.RemoveReviewWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(getCourses: GetCoursesUseCase, observeProgress: ObserveLearnerProgressUseCase, private val removeReviewWord: RemoveReviewWordUseCase) : ViewModel() {
    private val _words = MutableStateFlow<List<VocabularyWord>>(emptyList()); val words = _words.asStateFlow()
    init { viewModelScope.launch { combine(getCourses().let { kotlinx.coroutines.flow.flowOf(it) }, observeProgress()) { courses, progress -> courses.firstOrNull { it.id == progress.activeCourseId }?.lessons.orEmpty().flatMap { it.words }.filter { it.id in progress.reviewWordIds } }.collect { _words.value = it } } }
    fun mastered(id: String) = viewModelScope.launch { removeReviewWord(id) }
}
@Composable
fun ReviewRoute(onBack: () -> Unit, viewModel: ReviewViewModel = hiltViewModel()) {
    val words by viewModel.words.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Review mistakes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Words missed in quizzes are saved here on this device.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        if (words.isEmpty()) { Text("Your review deck is empty. Complete a quiz and missed words will appear here.", modifier = Modifier.padding(vertical = 24.dp)) }
        else LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(words, key = { it.id }) { word -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(word.sourceText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold); Text(word.targetText, color = MaterialTheme.colorScheme.primary); TextButton(onClick = { viewModel.mastered(word.id) }) { Text("I know this now") } } } } }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}
