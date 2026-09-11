package com.lingoleap.data.local

import android.content.Context
import com.google.gson.Gson
import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.Language
import com.lingoleap.domain.model.LanguagePair
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.model.Quiz
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningCatalogDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    private val catalog: Catalog by lazy {
        context.assets.open("learning_catalog.json").bufferedReader().use { Gson().fromJson(it, Catalog::class.java) }
    }
    fun languages(): List<Language> = catalog.languages
    fun languagePairs(): List<LanguagePair> = catalog.languagePairs
    fun courses(): List<Course> = catalog.courses
    fun quiz(lessonId: String): Quiz? = catalog.quizzes.firstOrNull { it.lessonId == lessonId }
    fun progress(): LearnerProgress = catalog.progress
}

data class Catalog(
    val languages: List<Language>, val languagePairs: List<LanguagePair>, val courses: List<Course>,
    val quizzes: List<Quiz>, val progress: LearnerProgress,
)
