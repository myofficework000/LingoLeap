package com.lingoleap.data.local.progress

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ProgressEntity::class], version = 3, exportSchema = false)
abstract class LingoLeapDatabase : RoomDatabase() {
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
    }
}
