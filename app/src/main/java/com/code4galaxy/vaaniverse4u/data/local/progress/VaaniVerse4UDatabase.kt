package com.code4galaxy.vaaniverse4u.data.local.progress

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ProgressEntity::class], version = 4, exportSchema = false)
abstract class VaaniVerse4UDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE learner_progress ADD COLUMN completedDailyChallengeIdsCsv TEXT NOT NULL DEFAULT ''"
                )
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE learner_progress ADD COLUMN reviewWordIdsCsv TEXT NOT NULL DEFAULT ''")
            }
        }

        /**
         * Releases before 1.0 seeded a fake Hindi greeting, a five-day streak, and
         * 120 XP to make prototype screenshots look populated. Remove that known
         * fixture while retaining any real lessons and XP earned during testing.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    UPDATE learner_progress
                    SET completedLessonIdsCsv = replace(replace(replace(completedLessonIdsCsv, 'en-hi-greetings,', ''), ',en-hi-greetings', ''), 'en-hi-greetings', ''),
                        streakDays = 0,
                        xp = MAX(xp - 120, 0)
                    WHERE instr(completedLessonIdsCsv, 'en-hi-greetings') > 0
                      AND streakDays >= 5
                      AND xp >= 120
                    """.trimIndent(),
                )
            }
        }
    }
}
