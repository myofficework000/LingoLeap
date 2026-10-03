package com.code4galaxy.vaaniverse4u.data.local

import android.content.Context
import com.google.gson.Gson
import com.code4galaxy.vaaniverse4u.domain.model.Course
import com.code4galaxy.vaaniverse4u.domain.model.Language
import com.code4galaxy.vaaniverse4u.domain.model.LanguagePair
import com.code4galaxy.vaaniverse4u.domain.model.LearnerProgress
import com.code4galaxy.vaaniverse4u.domain.model.Lesson
import com.code4galaxy.vaaniverse4u.domain.model.Quiz
import com.code4galaxy.vaaniverse4u.domain.model.VocabularyWord
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
    suspend fun quizzes(
        lessonId: String
    ): List<Quiz> = withContext(Dispatchers.IO) {

        catalog.toCourses()
            .asSequence()
            .flatMap { course ->
                course.lessons.asSequence()
            }
            .firstOrNull { lesson ->
                lesson.id == lessonId
            }
            ?.toQuizzes()
            ?: emptyList()
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
                            sourceText = if (isEnglishToRegional) { word.english
                            } else {
                                word.native
                            },
                            targetText = if (isEnglishToRegional) {
                                word.native
                            } else {
                                word.english
                            },
                            transliteration = word.transliteration
                        )
                    },
                    pronunciationLanguageTag =
                        languagesById[pair.targetLanguageId]
                            ?.locale
                            ?: "en-US"
                )
            },
        )
    }
}

private fun Lesson.toQuizzes(): List<Quiz> {

    if (words.isEmpty()) {
        return emptyList()
    }

    val targetChoices = words.map { it.targetText }
    val sourceChoices = words.map { it.sourceText }
    val forwardQuestions = words.mapIndexed { index, word ->
        Quiz(
            id = "quiz-$id-meaning-${index + 1}", lessonId = id,
            prompt = "What does ${word.sourceText} mean?",
            choices = rotateChoices(targetChoices, index), correctAnswer = word.targetText,
            pronunciationText = word.targetText, pronunciationLanguageTag = pronunciationLanguageTag,
        )
    }
    val reverseQuestions = words.mapIndexed { index, word ->
        Quiz(
            id = "quiz-$id-match-${index + 1}", lessonId = id,
            prompt = "Choose the matching word for ${word.targetText}",
            choices = rotateChoices(sourceChoices, index), correctAnswer = word.sourceText,
            pronunciationText = word.targetText, pronunciationLanguageTag = pronunciationLanguageTag,
        )
    }
    // Listening questions make every lesson a ten-question checkpoint even when
    // the beginner lesson has four core vocabulary words.
    val listeningQuestions = words.take(2).mapIndexed { index, word ->
        Quiz(
            id = "quiz-$id-listen-${index + 1}", lessonId = id,
            prompt = "Listen and choose the word you hear.",
            choices = rotateChoices(targetChoices, index + 1), correctAnswer = word.targetText,
            pronunciationText = word.targetText, pronunciationLanguageTag = pronunciationLanguageTag,
        )
    }
    return forwardQuestions + reverseQuestions + listeningQuestions
}

private fun rotateChoices(choices: List<String>, correctIndex: Int): List<String> =
    choices.drop(correctIndex) + choices.take(correctIndex)
