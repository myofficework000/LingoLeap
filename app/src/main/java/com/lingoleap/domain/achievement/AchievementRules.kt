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
        val currentProgress = when (definition.metric) {
            "completed_lessons" -> progress.completedLessonIds.size
            "streak_days" -> progress.streakDays
            "xp" -> progress.xp
            "daily_challenges" -> progress.completedDailyChallengeIds.size
            else -> 0
        }

        Achievement(
            id = definition.id,
            title = definition.title,
            description = definition.description,
            isUnlocked = currentProgress >= definition.target,
            currentProgress = currentProgress.coerceAtMost(definition.target),
            targetProgress = definition.target,
        )
    }

    private val defaultDefinitions = listOf(
        AchievementDefinition("first_lesson", "First lesson", "Complete your first lesson", "completed_lessons", 1),
        AchievementDefinition("five_lessons", "5 Lessons", "Complete 5 lessons", "completed_lessons", 5),
        AchievementDefinition("ten_lessons", "10 Lessons", "Complete 10 lessons", "completed_lessons", 10),
        AchievementDefinition("three_day_streak", "3 day streak", "Learn for three days in a row", "streak_days", 3),
        AchievementDefinition("seven_day_streak", "7 day streak", "Learn for seven days in a row", "streak_days", 7),
        AchievementDefinition("fourteen_day_streak", "14 Day Streak", "Learn for fourteen days in a row", "streak_days", 14),
        AchievementDefinition("xp_100", "100 XP", "Earn 100 experience points", "xp", 100),
        AchievementDefinition("xp_250", "250 XP", "Earn 250 experience points", "xp", 250),
        AchievementDefinition("xp_500", "500 XP", "Earn 500 experience points", "xp", 500),
        AchievementDefinition("daily_challenges_3", "Daily Momentum", "Complete three offline daily challenges", "daily_challenges", 3),
    )
}
