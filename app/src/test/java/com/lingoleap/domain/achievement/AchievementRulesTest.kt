package com.lingoleap.domain.achievement

import com.lingoleap.domain.model.LearnerProgress
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
}
