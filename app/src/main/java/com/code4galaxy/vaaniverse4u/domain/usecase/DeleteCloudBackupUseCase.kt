package com.code4galaxy.vaaniverse4u.domain.usecase

import com.code4galaxy.vaaniverse4u.domain.repository.CloudBackupRepository
import javax.inject.Inject

class DeleteCloudBackupUseCase @Inject constructor(
    private val repository: CloudBackupRepository,
) {
    suspend operator fun invoke() = repository.deleteCloudBackup()
}
