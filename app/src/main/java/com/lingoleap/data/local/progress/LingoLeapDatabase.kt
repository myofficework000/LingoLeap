package com.lingoleap.data.local.progress

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProgressEntity::class], version = 1, exportSchema = false)
abstract class LingoLeapDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
}
