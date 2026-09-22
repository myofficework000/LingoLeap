package com.lingoleap.domain.achievement

import com.lingoleap.domain.model.LearnerProgress
import com.lingoleap.domain.model.AchievementDefinition
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementRulesTest {
    @Test fun `unlocks achievements from progress milestones`() {
        val achievements = AchievementRules.evaluate(
            LearnerProgress("course", setOf("lesson-1"), streakDays = 7, xp = 100),
        )
        assertTrue(achievements.all { it.isUnlocked })
    }

    @Test fun `keeps achievements locked without milestones`() {
        val achievements = AchievementRules.evaluate(
            LearnerProgress("course", emptySet(), streakDays = 0, xp = 0),
        )
        assertFalse(achievements.any { it.isUnlocked })
    }

    @Test fun `evaluates JSON-style daily challenge definition`() {
        val achievement = AchievementRules.evaluate(
            progress = LearnerProgress(
                activeCourseId = "course-en-hi",
                completedLessonIds = emptySet(),
                completedDailyChallengeIds = setOf("hi-greeting", "hi-thanks", "hi-water"),
                streakDays = 0,
                xp = 0,
            ),
            definitions = listOf(
                AchievementDefinition(
                    id = "daily_challenges_3",
                    title = "Daily Momentum",
                    description = "Complete three offline daily challenges.",
                    metric = "daily_challenges",
                    target = 3,
                ),
            ),
        ).single()

        assertTrue(achievement.isUnlocked)
    }
}
