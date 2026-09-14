package com.lingoleap.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import com.lingoleap.data.local.progress.LingoLeapDatabase
import com.lingoleap.data.local.progress.ProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile("lingoleap_preferences") }

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LingoLeapDatabase =
        Room.databaseBuilder(context, LingoLeapDatabase::class.java, "lingoleap.db").fallbackToDestructiveMigration(dropAllTables = true).build()

    @Provides fun provideProgressDao(database: LingoLeapDatabase): ProgressDao = database.progressDao()
}
