package com.lingoleap.domain.achievement

import com.lingoleap.domain.model.Achievement
import com.lingoleap.domain.model.LearnerProgress

object AchievementRules {
    fun evaluate(progress: LearnerProgress): List<Achievement> = listOf(
        Achievement("first_lesson", "First lesson", "Complete your first lesson", progress.completedLessonIds.isNotEmpty()),
        Achievement("three_day_streak", "3 day streak", "Learn for three days in a row", progress.streakDays >= 3),
        Achievement("seven_day_streak", "7 day streak", "Learn for seven days in a row", progress.streakDays >= 7),
        Achievement("xp_100", "100 XP", "Earn 100 experience points", progress.xp >= 100),
    )
}
