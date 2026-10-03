package com.code4galaxy.vaaniverse4u.domain.repository

/** Controls the optional cloud backup without affecting offline learning. */
interface CloudBackupRepository {
    suspend fun deleteCloudBackup()
}
