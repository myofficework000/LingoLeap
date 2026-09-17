package com.lingoleap.domain.usecase

import com.lingoleap.domain.model.Lesson

object LessonRules {

    fun isLocked(lesson: Lesson, lessons: List<Lesson>, completedLessonIds: Set<String>): Boolean {

        val orderedLessons = lessons.sortedBy { it.order }

        val currentIndex = orderedLessons.indexOfFirst { it.id == lesson.id }

        if (currentIndex <= 0) { return false }

        val previousLesson = orderedLessons[currentIndex - 1]

        return previousLesson.id !in completedLessonIds
    }

    fun nextWordIndex(currentIndex: Int, wordCount: Int): Int {
        if (wordCount == 0) { return 0 }
        return (currentIndex + 1).coerceAtMost(wordCount - 1)
    }

    fun previousWordIndex(currentIndex: Int): Int {
        return (currentIndex - 1).coerceAtLeast(0)
    }

    fun canComplete(lesson: Lesson, viewedWordIds: Set<String>): Boolean {

        if (lesson.words.isEmpty()) { return false }
        return lesson.words.all { it.id in viewedWordIds }
    }
}