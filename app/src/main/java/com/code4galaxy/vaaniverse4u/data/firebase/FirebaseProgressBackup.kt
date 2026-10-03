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
    private var observedUid: String? = null

    fun start() {
        auth.addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let { user -> synchronizeUser(user.uid, user.isAnonymous) }
        }
        if (auth.currentUser == null) auth.signInAnonymously()
    }

    private fun synchronizeUser(uid: String, isAnonymous: Boolean) {
        if (observedUid == uid && backupJob?.isActive == true) return
        backupJob?.cancel()
        observedUid = uid
        backupJob = scope.launch {
            // A cloud read is best effort. Connectivity, a stale Firebase session, or a
            // temporary rules error must never take down the offline-first learning app.
            // The local Room state remains usable and is synced once the next update occurs.
            if (!isAnonymous) {
                runCatching { restoreCloudProgress(uid) }
            }
            learningRepository.observeProgress().collect { progress -> backup(uid, progress) }
        }
    }

    private suspend fun restoreCloudProgress(uid: String) {
        val userDocument = firestore.collection("users").document(uid).get().await()
        val activeCourseId = userDocument.getString("activeCourseId") ?: return
        val progressDocument = userDocument.reference.collection("courses").document(activeCourseId).get().await()
        if (!progressDocument.exists()) return
        val restored = LearnerProgress(
            activeCourseId = progressDocument.getString("activeCourseId") ?: activeCourseId,
            completedLessonIds = (progressDocument.get("completedLessonIds") as? List<*>)
                ?.filterIsInstance<String>()?.toSet().orEmpty(),
            completedDailyChallengeIds = (progressDocument.get("completedDailyChallengeIds") as? List<*>)
                ?.filterIsInstance<String>()?.toSet().orEmpty(),
            reviewWordIds = (progressDocument.get("reviewWordIds") as? List<*>)
                ?.filterIsInstance<String>()?.toSet().orEmpty(),
            streakDays = (progressDocument.getLong("streakDays") ?: 0L).toInt(),
            xp = (progressDocument.getLong("xp") ?: 0L).toInt(),
        ).withoutLegacyDemoSeed()
        learningRepository.replaceProgress(restored)
    }

    private fun backup(uid: String, progress: LearnerProgress) {
        val user = firestore.collection("users").document(uid)
        user.set(
            mapOf(
                "uid" to uid,
                "activeCourseId" to progress.activeCourseId,
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
     * Removes the cloud learning backup. A guest identity can safely be removed too;
     * an email or Google account is deliberately retained because Firebase requires a
     * recent re-authentication before an account can be deleted. Offline content and
     * local progress remain on the device.
     */
    override suspend fun deleteCloudBackup() {
        val user = auth.currentUser ?: return
        backupJob?.cancel()
        backupJob = null
        observedUid = null

        val userDocument = firestore.collection("users").document(user.uid)
        val courseDocuments = userDocument.collection("courses").get().await().documents
        firestore.runBatch { batch ->
            courseDocuments.forEach { document -> batch.delete(document.reference) }
            batch.delete(userDocument)
        }.await()
        if (user.isAnonymous) {
            user.delete().await()
        }
    }
}

/** Removes only the identifiable pre-release demonstration fixture from a backup. */
private fun LearnerProgress.withoutLegacyDemoSeed(): LearnerProgress {
    val containsDemoSeed = "en-hi-greetings" in completedLessonIds && streakDays >= 5 && xp >= 120
    return if (containsDemoSeed) {
        copy(
            completedLessonIds = completedLessonIds - "en-hi-greetings",
            streakDays = 0,
            xp = (xp - 120).coerceAtLeast(0),
        )
    } else {
        this
    }
}
