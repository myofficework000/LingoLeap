package com.code4galaxy.vaaniverse4u.data.firebase

import com.code4galaxy.vaaniverse4u.domain.model.LearnerProgress
import com.code4galaxy.vaaniverse4u.domain.repository.CloudBackupRepository
import com.code4galaxy.vaaniverse4u.domain.repository.LearningRepository
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backs up the local-first learner state after anonymous Firebase sign-in.
 * JSON assets and Room remain the source of truth, so a failed network request never blocks learning.
 */
@Singleton
class FirebaseProgressBackup @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val learningRepository: LearningRepository,
) : CloudBackupRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var backupJob: Job? = null

    fun start() {
        auth.currentUser?.uid?.let(::observeAndBackup)
            ?: auth.signInAnonymously().addOnSuccessListener { result ->
                result.user?.uid?.let(::observeAndBackup)
            }
    }

    private fun observeAndBackup(uid: String) {
        if (backupJob != null) return
        backupJob = scope.launch {
            learningRepository.observeProgress().collect { progress -> backup(uid, progress) }
        }
    }

    private fun backup(uid: String, progress: LearnerProgress) {
        val user = firestore.collection("users").document(uid)
        user.set(
            mapOf(
                "uid" to uid,
                "updatedAt" to FieldValue.serverTimestamp(),
                "syncVersion" to 1,
            ),
            SetOptions.merge(),
        )
        user.collection("courses").document(progress.activeCourseId).set(
            mapOf(
                "activeCourseId" to progress.activeCourseId,
                "completedLessonIds" to progress.completedLessonIds.sorted(),
                "completedDailyChallengeIds" to progress.completedDailyChallengeIds.sorted(),
                "reviewWordIds" to progress.reviewWordIds.sorted(),
                "streakDays" to progress.streakDays,
                "xp" to progress.xp,
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        )
    }

    /**
     * Removes the anonymous Firebase Auth user and the only Firestore documents
     * VaaniVerse4U creates for that user. Offline content and local progress remain.
     */
    override suspend fun deleteCloudBackup() {
        val user = auth.currentUser ?: return
        backupJob?.cancel()
        backupJob = null

        val userDocument = firestore.collection("users").document(user.uid)
        val courseDocuments = userDocument.collection("courses").get().await().documents
        firestore.runBatch { batch ->
            courseDocuments.forEach { document -> batch.delete(document.reference) }
            batch.delete(userDocument)
        }.await()
        user.delete().await()
    }
}
