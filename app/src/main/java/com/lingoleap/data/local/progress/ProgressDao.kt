package com.lingoleap.data.local.progress

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM learner_progress WHERE id = :id") suspend fun get(id: Int = ProgressEntity.SINGLETON_ID): ProgressEntity?
    @Query("SELECT * FROM learner_progress WHERE id = :id") fun observe(id: Int = ProgressEntity.SINGLETON_ID): Flow<ProgressEntity?>
    @Upsert suspend fun upsert(progress: ProgressEntity)
}
