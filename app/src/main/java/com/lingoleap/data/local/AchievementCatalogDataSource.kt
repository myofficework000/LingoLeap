package com.lingoleap.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lingoleap.domain.model.AchievementDefinition
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementCatalogDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val definitions: List<AchievementDefinition> by lazy {
        context.assets.open(ASSET_NAME).bufferedReader().use { reader ->
            Gson().fromJson(reader, object : TypeToken<List<AchievementDefinition>>() {}.type)
        }
    }

    suspend fun definitions(): List<AchievementDefinition> = withContext(Dispatchers.IO) { definitions }

    private companion object {
        const val ASSET_NAME = "achievements.json"
    }
}
