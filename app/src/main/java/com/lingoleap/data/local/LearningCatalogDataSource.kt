package com.lingoleap.data.local

import android.content.Context
import com.google.gson.Gson
import com.lingoleap.domain.model.Course
import com.lingoleap.domain.model.Language
import com.lingoleap.domain.model.LanguagePair
import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.model.Lesson
import com.lingoleap.domain.model.Quiz
import com.lingoleap.domain.model.VocabularyWord
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
    suspend fun courses(): List<Course> = withContext(Dispatchers.IO) { catalog.toCourses() }
    suspend fun quiz(lessonId: String): Quiz? = withContext(Dispatchers.IO) {
        catalog.toCourses()
            .asSequence()
            .flatMap { course -> course.lessons.asSequence() }
            .firstOrNull { lesson -> lesson.id == lessonId }
            ?.toQuiz()
    }
    suspend fun progress(): LearnerProgress = withContext(Dispatchers.IO) { catalog.progress }
}

data class Catalog(
    val catalogVersion: String,
    val defaultLanguagePairId: String,
    val languages: List<Language>,
    val languagePairs: List<LanguagePair>,
    val curricula: List<LanguageCurriculum>,
    val progress: LearnerProgress,
)

data class LanguageCurriculum(
    val languageId: String,
    val lessons: List<CurriculumLesson>,
)

data class CurriculumLesson(
    val key: String,
    val title: String,
    val titleNative: String,
    val words: List<CurriculumWord>,
)

data class CurriculumWord(
    val english: String,
    val native: String,
    val transliteration: String? = null,
)

private fun Catalog.toCourses(): List<Course> {
    val languagesById = languages.associateBy { it.id }
    val curriculaByLanguageId = curricula.associateBy { it.languageId }

    return languagePairs.mapNotNull { pair ->
        val regionalLanguageId = if (pair.sourceLanguageId == "en") {
            pair.targetLanguageId
        } else {
            pair.sourceLanguageId
        }
        val regionalLanguage = languagesById[regionalLanguageId] ?: return@mapNotNull null
        val curriculum = curriculaByLanguageId[regionalLanguageId] ?: return@mapNotNull null
        val isEnglishToRegional = pair.sourceLanguageId == "en"

        Course(
            id = "course-${pair.id}",
            languagePairId = pair.id,
            title = if (isEnglishToRegional) {
                "English to ${regionalLanguage.name}: First Steps"
            } else {
                "${regionalLanguage.name} to English: First Steps"
            },
            lessons = curriculum.lessons.mapIndexed { index, contentLesson ->
                Lesson(
                    id = "${pair.id}-${contentLesson.key}",
                    title = contentLesson.title,
                    order = index + 1,
                    words = contentLesson.words.mapIndexed { wordIndex, word ->
                        VocabularyWord(
                            id = "${pair.id}-${contentLesson.key}-${wordIndex + 1}",
                            sourceText = if (isEnglishToRegional) word.english else word.native,
                            targetText = if (isEnglishToRegional) word.native else word.english,
                            transliteration = word.transliteration,
                        )
                    },
                )
            },
        )
    }
}

private fun Lesson.toQuiz(): Quiz? {
    val correctWord = words.firstOrNull() ?: return null
    return Quiz(
        id = "quiz-$id",
        lessonId = id,
        prompt = "Choose the correct meaning of ${correctWord.sourceText}",
        choices = words.map { word -> word.targetText },
        correctAnswer = correctWord.targetText,
    )
}
