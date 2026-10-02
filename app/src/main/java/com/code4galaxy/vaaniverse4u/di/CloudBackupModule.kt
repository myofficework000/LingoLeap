package com.code4galaxy.vaaniverse4u.di

import com.code4galaxy.vaaniverse4u.data.firebase.FirebaseProgressBackup
import com.code4galaxy.vaaniverse4u.domain.repository.CloudBackupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CloudBackupModule {
    @Binds
    abstract fun bindCloudBackupRepository(
        implementation: FirebaseProgressBackup,
    ): CloudBackupRepository
}
