package com.lingoleap.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lingoleap.domain.model.DailyChallenge
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyChallengeDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val challenges: List<DailyChallenge> by lazy {
        context.assets.open(ASSET_NAME).bufferedReader().use { reader ->
            Gson().fromJson(reader, object : TypeToken<List<DailyChallenge>>() {}.type)
        }
    }

    suspend fun challenges(): List<DailyChallenge> = withContext(Dispatchers.IO) {
        challenges
    }

    private companion object {
        const val ASSET_NAME = "daily_challenges.json"
    }
}
