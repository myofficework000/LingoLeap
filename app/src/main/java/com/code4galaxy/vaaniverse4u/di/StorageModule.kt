package com.code4galaxy.vaaniverse4u.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import com.code4galaxy.vaaniverse4u.data.local.progress.VaaniVerse4UDatabase
import com.code4galaxy.vaaniverse4u.data.local.progress.ProgressDao
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
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile("vaaniverse4u_preferences") }

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VaaniVerse4UDatabase =
        Room.databaseBuilder(context, VaaniVerse4UDatabase::class.java, "vaaniverse4u.db")
            .addMigrations(VaaniVerse4UDatabase.MIGRATION_1_2)
            .addMigrations(VaaniVerse4UDatabase.MIGRATION_2_3)
            .addMigrations(VaaniVerse4UDatabase.MIGRATION_3_4)
            .build()

    @Provides fun provideProgressDao(database: VaaniVerse4UDatabase): ProgressDao = database.progressDao()
}
