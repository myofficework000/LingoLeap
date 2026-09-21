package com.lingoleap.domain.achievement

import com.lingoleap.domain.model.Achievement
import com.lingoleap.domain.model.LearnerProgress

object AchievementRules {

    fun evaluate(progress: LearnerProgress): List<Achievement> {

        val completedLessons = progress.completedLessonIds.size

        return listOf(

            Achievement(
                id = "first_lesson",
                title = "First lesson",
                description = "Complete your first lesson",
                isUnlocked = completedLessons >= 1,
                currentProgress = completedLessons.coerceAtMost(1),
                targetProgress = 1
            ),

            Achievement(
                id = "five_lessons",
                title = "5 Lessons",
                description = "Complete 5 lessons",
                isUnlocked = completedLessons >= 5,
                currentProgress = completedLessons.coerceAtMost(5),
                targetProgress = 5
            ),

            Achievement(
                id = "ten_lessons",
                title = "10 Lessons",
                description = "Complete 10 lessons",
                isUnlocked = completedLessons >= 10,
                currentProgress = completedLessons.coerceAtMost(10),
                targetProgress = 10
            ),

            Achievement(
                id = "three_day_streak",
                title = "3 day streak",
                description = "Learn for three days in a row",
                isUnlocked = progress.streakDays >= 3,
                currentProgress = progress.streakDays.coerceAtMost(3),
                targetProgress = 3
            ),

            Achievement(
                id = "seven_day_streak",
                title = "7 day streak",
                description = "Learn for seven days in a row",
                isUnlocked = progress.streakDays >= 7,
                currentProgress = progress.streakDays.coerceAtMost(7),
                targetProgress = 7
            ),

            Achievement(
                id = "fourteen_day_streak",
                title = "14 Day Streak",
                description = "Learn for fourteen days in a row",
                isUnlocked = progress.streakDays >= 14,
                currentProgress = progress.streakDays.coerceAtMost(14),
                targetProgress = 14
            ),

            Achievement(
                id = "xp_100",
                title = "100 XP",
                description = "Earn 100 experience points",
                isUnlocked = progress.xp >= 100,
                currentProgress = progress.xp.coerceAtMost(100),
                targetProgress = 100
            ),

            Achievement(
                id = "xp_250",
                title = "250 XP",
                description = "Earn 250 experience points",
                isUnlocked = progress.xp >= 250,
                currentProgress = progress.xp.coerceAtMost(250),
                targetProgress = 250
            ),

            Achievement(
                id = "xp_500",
                title = "500 XP",
                description = "Earn 500 experience points",
                isUnlocked = progress.xp >= 500,
                currentProgress = progress.xp.coerceAtMost(500),
                targetProgress = 500
            )
        )
    }
}
