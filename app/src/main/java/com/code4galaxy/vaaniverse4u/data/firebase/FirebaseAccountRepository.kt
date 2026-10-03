package com.code4galaxy.vaaniverse4u.data.firebase

import com.code4galaxy.vaaniverse4u.domain.repository.AccountRepository
import com.code4galaxy.vaaniverse4u.domain.repository.LearnerAccount
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Links a guest account whenever possible so its UID—and therefore its progress
 * backup—survives account creation. Existing accounts are signed in normally;
 * FirebaseProgressBackup then restores their cloud state.
 */
@Singleton
class FirebaseAccountRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AccountRepository {

    override fun observeAccount(): Flow<LearnerAccount> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser.toLearnerAccount())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun createEmailAccount(displayName: String, email: String, password: String): LearnerAccount {
        val credential = EmailAuthProvider.getCredential(email.trim(), password)
        val user = auth.currentUser
        val accountUser = if (user?.isAnonymous == true) {
            user.linkWithCredential(credential).await().user
        } else {
            auth.createUserWithEmailAndPassword(email.trim(), password).await().user
        } ?: error("Firebase did not return an account")
        saveProfile(accountUser, displayName.trim())
        return accountUser.toLearnerAccount(displayName.trim())
    }

    override suspend fun signInWithEmail(email: String, password: String): LearnerAccount {
        val accountUser = auth.signInWithEmailAndPassword(email.trim(), password).await().user
            ?: error("Firebase did not return an account")
        return accountUser.toLearnerAccount()
    }

    override suspend fun signInWithGoogle(idToken: String): LearnerAccount {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val user = auth.currentUser
        val accountUser = if (user?.isAnonymous == true) {
            runCatching { user.linkWithCredential(credential).await().user }
                .getOrElse { auth.signInWithCredential(credential).await().user }
        } else {
            auth.signInWithCredential(credential).await().user
        } ?: error("Firebase did not return an account")
        saveProfile(accountUser, accountUser.displayName.orEmpty())
        return accountUser.toLearnerAccount()
    }

    override suspend fun signOutToGuest() {
        auth.signOut()
        auth.signInAnonymously().await()
    }

    private suspend fun saveProfile(user: FirebaseUser, requestedName: String) {
        firestore.collection("users").document(user.uid).set(
            mapOf(
                "uid" to user.uid,
                "displayName" to requestedName.ifBlank { user.displayName.orEmpty() },
                "email" to user.email,
                "isAnonymous" to user.isAnonymous,
                "providers" to user.providerData.mapNotNull { it.providerId }.distinct(),
                "updatedAt" to FieldValue.serverTimestamp(),
            ),
            SetOptions.merge(),
        ).await()
    }
}

private fun FirebaseUser?.toLearnerAccount(requestedName: String? = null): LearnerAccount {
    if (this == null) return LearnerAccount()
    val emailProvider = providerData.any { it.providerId == "password" }
    val googleProvider = providerData.any { it.providerId == "google.com" }
    return LearnerAccount(
        uid = uid,
        displayName = requestedName?.ifBlank { null } ?: displayName?.ifBlank { null }
            ?: email?.substringBefore('@') ?: "Learner",
        email = email,
        isAnonymous = isAnonymous,
        provider = when {
            isAnonymous -> "Guest"
            googleProvider -> "Google"
            emailProvider -> "Email account"
            else -> "Account"
        },
    )
}
