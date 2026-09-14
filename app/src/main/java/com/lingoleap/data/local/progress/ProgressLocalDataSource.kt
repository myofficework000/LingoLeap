package com.lingoleap.data.local.progress

import com.lingoleap.domain.model.LearnerProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProgressLocalDataSource @Inject constructor(private val dao: ProgressDao) {
    suspend fun get(): LearnerProgress? = dao.get()?.toDomain()
    fun observe(): Flow<LearnerProgress> = dao.observe().filterNotNull().map { it.toDomain() }
    suspend fun save(progress: LearnerProgress) = dao.upsert(progress.toEntity())
}

private fun ProgressEntity.toDomain() = LearnerProgress(
    activeCourseId = activeCourseId,
    completedLessonIds = completedLessonIdsCsv.split(',').filter { it.isNotBlank() }.toSet(),
    streakDays = streakDays,
    xp = xp,
)

private fun LearnerProgress.toEntity() = ProgressEntity(
    activeCourseId = activeCourseId,
    completedLessonIdsCsv = completedLessonIds.sorted().joinToString(","),
    streakDays = streakDays,
    xp = xp,
)
