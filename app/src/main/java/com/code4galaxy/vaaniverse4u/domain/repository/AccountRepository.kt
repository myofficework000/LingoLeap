package com.code4galaxy.vaaniverse4u.domain.repository

import kotlinx.coroutines.flow.Flow

data class LearnerAccount(
    val uid: String? = null,
    val displayName: String = "Learner",
    val email: String? = null,
    val isAnonymous: Boolean = true,
    val provider: String = "Guest",
)

interface AccountRepository {
    fun observeAccount(): Flow<LearnerAccount>
    suspend fun createEmailAccount(displayName: String, email: String, password: String): LearnerAccount
    suspend fun signInWithEmail(email: String, password: String): LearnerAccount
    suspend fun signInWithGoogle(idToken: String): LearnerAccount
    suspend fun signOutToGuest()
}
