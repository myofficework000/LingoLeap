package com.code4galaxy.vaaniverse4u.presentation.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code4galaxy.vaaniverse4u.domain.repository.AccountRepository
import com.code4galaxy.vaaniverse4u.domain.repository.LearnerAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuthException

data class AccountState(
    val account: LearnerAccount = LearnerAccount(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AccountState())
    val state: StateFlow<AccountState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.observeAccount().collectLatest { account ->
                _state.update { it.copy(account = account, isLoading = false) }
            }
        }
    }

    fun createAccount(displayName: String, email: String, password: String) = runAccountAction("Account created. Your existing progress is now protected.") {
        accountRepository.createEmailAccount(displayName, email, password)
    }

    fun signIn(email: String, password: String) = runAccountAction("Signed in. Restoring your cloud learning progress.") {
        accountRepository.signInWithEmail(email, password)
    }

    fun signInWithGoogle(idToken: String) = runAccountAction("Google account connected. Your progress is syncing.") {
        accountRepository.signInWithGoogle(idToken)
    }

    fun signOut() = runAccountAction("Signed out. You can keep learning as a guest on this device.") {
        accountRepository.signOutToGuest()
        LearnerAccount()
    }

    private fun runAccountAction(successMessage: String, action: suspend () -> LearnerAccount) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, message = null, isError = false) }
            runCatching { action() }
                .onSuccess { _state.update { current -> current.copy(account = it, isLoading = false, message = successMessage, isError = false) } }
                .onFailure { error ->
                    _state.update { current ->
                        current.copy(
                            isLoading = false,
                            message = error.toLearnerMessage(),
                            isError = true,
                        )
                    }
                }
        }
    }
}

private fun Throwable.toLearnerMessage(): String = when ((this as? FirebaseAuthException)?.errorCode) {
    "ERROR_OPERATION_NOT_ALLOWED" ->
        "Email sign-in is not enabled yet. We’ll turn it on securely in Firebase, then you can create your account."
    "ERROR_EMAIL_ALREADY_IN_USE" ->
        "An account already exists with this email. Choose Sign in instead."
    "ERROR_INVALID_EMAIL" ->
        "Please enter a valid email address."
    "ERROR_WEAK_PASSWORD" ->
        "Choose a password with at least 6 characters."
    "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" ->
        "That email or password does not match an account."
    else -> "We couldn’t complete that account action. Check your connection and try again."
}
