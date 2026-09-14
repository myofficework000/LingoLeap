package com.lingoleap.data.local.progress

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learner_progress")
data class ProgressEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val activeCourseId: String,
    val completedLessonIdsCsv: String,
    val streakDays: Int,
    val xp: Int,
) {
    companion object { const val SINGLETON_ID = 1 }
}
