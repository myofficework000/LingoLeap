package com.lingoleap.domain.achievement

import com.lingoleap.domain.model.Achievement
import com.lingoleap.domain.model.AchievementDefinition
import com.lingoleap.domain.model.LearnerProgress

object AchievementRules {
    fun evaluate(progress: LearnerProgress): List<Achievement> = evaluate(progress, defaultDefinitions)

    fun evaluate(
        progress: LearnerProgress,
        definitions: List<AchievementDefinition>,
    ): List<Achievement> = definitions.map { definition ->
        Achievement(
            id = definition.id,
            title = definition.title,
            description = definition.description,
            isUnlocked = when (definition.metric) {
                "completed_lessons" -> progress.completedLessonIds.size >= definition.target
                "streak_days" -> progress.streakDays >= definition.target
                "xp" -> progress.xp >= definition.target
                "daily_challenges" -> progress.completedDailyChallengeIds.size >= definition.target
                else -> false
            },
        )
    }

    private val defaultDefinitions = listOf(
        AchievementDefinition("first_lesson", "First lesson", "Complete your first lesson", "completed_lessons", 1),
        AchievementDefinition("three_day_streak", "3 day streak", "Learn for three days in a row", "streak_days", 3),
        AchievementDefinition("seven_day_streak", "7 day streak", "Learn for seven days in a row", "streak_days", 7),
        AchievementDefinition("xp_100", "100 XP", "Earn 100 experience points", "xp", 100),
    )
}
