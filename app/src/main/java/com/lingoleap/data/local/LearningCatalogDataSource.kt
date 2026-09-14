package com.lingoleap.data.local

import android.content.Context
import com.google.gson.Gson
import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.Language
import com.lingoleap.domain.model.LanguagePair
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.model.Quiz
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningCatalogDataSource @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val catalog: Catalog by lazy {
        context.assets.open("learning_catalog.json").bufferedReader().use { Gson().fromJson(it, Catalog::class.java) }
    }
    suspend fun languages(): List<Language> = withContext(Dispatchers.IO) { catalog.languages }
    suspend fun languagePairs(): List<LanguagePair> = withContext(Dispatchers.IO) { catalog.languagePairs }
    suspend fun courses(): List<Course> = withContext(Dispatchers.IO) { catalog.courses }
    suspend fun quiz(lessonId: String): Quiz? = withContext(Dispatchers.IO) { catalog.quizzes.firstOrNull { it.lessonId == lessonId } }
    suspend fun progress(): LearnerProgress = withContext(Dispatchers.IO) { catalog.progress }
}

data class Catalog(
    val languages: List<Language>, val languagePairs: List<LanguagePair>, val courses: List<Course>,
    val quizzes: List<Quiz>, val progress: LearnerProgress,
)
