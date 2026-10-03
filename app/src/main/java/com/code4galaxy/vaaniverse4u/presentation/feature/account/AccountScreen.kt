package com.code4galaxy.vaaniverse4u.presentation.feature.account

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.code4galaxy.vaaniverse4u.domain.repository.LearnerAccount
import com.code4galaxy.vaaniverse4u.R
import kotlinx.coroutines.launch

private val AccountNavy = Color(0xFF082C59)
private val AccountGreen = Color(0xFF08784E)

@Composable
fun AccountRoute(onBack: () -> Unit, viewModel: AccountViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AccountScreen(
        state = state,
        onBack = onBack,
        onCreateAccount = viewModel::createAccount,
        onSignIn = viewModel::signIn,
        onGoogleToken = viewModel::signInWithGoogle,
        onSignOut = viewModel::signOut,
    )
}

@Composable
private fun AccountScreen(
    state: AccountState,
    onBack: () -> Unit,
    onCreateAccount: (String, String, String) -> Unit,
    onSignIn: (String, String) -> Unit,
    onGoogleToken: (String) -> Unit,
    onSignOut: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isSignIn by rememberSaveable { mutableStateOf(false) }
    var localGoogleError by remember { mutableStateOf<String?>(null) }
    val googleClientResource = remember(context) {
        context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    }
    val feedbackMessage = state.message ?: localGoogleError
    val feedbackIsError = state.isError || localGoogleError != null

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFFFFDF7)) {
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.account_heritage_hero),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(224.dp),
            )
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AccountNavy)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Your account", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = AccountNavy)
                Text("Keep every small step, everywhere", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF526477))
            }
        }
        Spacer(Modifier.height(76.dp))

        if (!state.account.isAnonymous) {
            SignedInCard(state.account, onSignOut, onBack)
        } else {
            AccountPromiseCard()
            Spacer(Modifier.height(18.dp))
            AccountModeTabs(isSignIn = isSignIn, onChange = { isSignIn = it })
            Spacer(Modifier.height(20.dp))
            Text(if (isSignIn) "Welcome back" else "Create your learning home", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = AccountNavy)
            Spacer(Modifier.height(4.dp))
            Text(
                if (isSignIn) "Sign in to restore your cloud backup." else "Your saved lessons, streak, and XP will follow you to every device.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF526477),
            )
            Spacer(Modifier.height(20.dp))

            if (!isSignIn) {
                AccountField("Display name", name, { name = it }, "e.g. Abhishek")
                Spacer(Modifier.height(12.dp))
            }
            AccountField("Email address", email, { email = it }, "you@example.com")
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Password") },
                supportingText = { Text("Use at least 6 characters.") },
                visualTransformation = PasswordVisualTransformation(),
                colors = accountTextFieldColors(),
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isSignIn) onSignIn(email, password) else onCreateAccount(name, email, password)
                },
                enabled = email.isNotBlank() && password.length >= 6 && (isSignIn || name.isNotBlank()) && !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccountGreen),
            ) {
                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                else Text(if (isSignIn) "Sign in with email" else "Create account", fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(14.dp))
            GoogleButton(
                enabled = !state.isLoading,
                onClick = {
                    if (googleClientResource == 0) {
                        localGoogleError = "Google sign-in is being configured for this app. Please use email while it is enabled."
                        return@GoogleButton
                    }
                    val activity = context as? Activity
                    if (activity == null) {
                        localGoogleError = "Google sign-in needs an Android activity."
                        return@GoogleButton
                    }
                    scope.launch {
                        runCatching {
                            val option = GetGoogleIdOption.Builder()
                                .setServerClientId(context.getString(googleClientResource))
                                .setFilterByAuthorizedAccounts(false)
                                .setAutoSelectEnabled(false)
                                .build()
                            val result = credentialManager.getCredential(
                                activity,
                                GetCredentialRequest.Builder().addCredentialOption(option).build(),
                            )
                            val credential = result.credential as? CustomCredential
                                ?: error("Google did not return an account credential")
                            check(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) { "Unexpected Google credential" }
                            GoogleIdTokenCredential.createFrom(credential.data).idToken
                        }.onSuccess(onGoogleToken).onFailure { error ->
                            localGoogleError = error.message ?: "Google sign-in could not be completed."
                        }
                    }
                },
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { isSignIn = !isSignIn },
                colors = ButtonDefaults.textButtonColors(contentColor = AccountGreen),
            ) { Text(if (isSignIn) "Create a new account" else "I already have an account", fontWeight = FontWeight.ExtraBold) }
        }

            Spacer(Modifier.height(20.dp))
            }
            feedbackMessage?.let { message ->
                AccountFeedback(
                    message = message,
                    isError = feedbackIsError,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp),
                )
            }
        }
    }
}

@Composable
private fun AccountPromiseCard() {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFFFC)), shape = RoundedCornerShape(26.dp), elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = Color(0xFFE2F6E8)) {
                Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.padding(13.dp), tint = AccountGreen)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Keep every small step", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = AccountNavy)
                Text("Create an account and your learning is safely with you on every device.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF526477))
            }
        }
    }
}

@Composable
private fun AccountModeTabs(isSignIn: Boolean, onChange: (Boolean) -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = Color(0xFFF3F5F0)) {
        Row(modifier = Modifier.padding(4.dp)) {
            AccountTab("Create account", !isSignIn, Modifier.weight(1f)) { onChange(false) }
            AccountTab("Sign in", isSignIn, Modifier.weight(1f)) { onChange(true) }
        }
    }
}

@Composable
private fun AccountTab(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier.clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick), shape = RoundedCornerShape(14.dp), color = if (selected) AccountGreen else Color.Transparent) {
        Text(text, modifier = Modifier.padding(vertical = 12.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = if (selected) Color.White else AccountNavy, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun SignedInCard(account: LearnerAccount, onSignOut: () -> Unit, onContinue: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F6ED)), shape = RoundedCornerShape(22.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = Color.White) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.padding(8.dp), tint = AccountGreen)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(account.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = AccountNavy)
                    Text(account.email ?: account.provider, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF526477))
                }
                Icon(Icons.Default.CloudDone, contentDescription = "Cloud backup active", tint = AccountGreen)
            }
            Spacer(Modifier.height(16.dp))
            Text("Cloud backup is active", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = AccountGreen)
            Text("Your lessons, XP, streak, and review words sync to this Firebase account.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF526477))
            Spacer(Modifier.height(16.dp))
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = AccountGreen)) {
                Text("Back to profile", fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onSignOut, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = AccountNavy)) { Text("Sign out and continue as guest") }
        }
    }
}

@Composable
private fun AccountFeedback(message: String, isError: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (isError) Color(0xFFFFE9E6) else Color(0xFFE2F6E8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(34.dp), shape = CircleShape, color = Color.White) {
                Icon(
                    imageVector = if (isError) Icons.Default.AccountCircle else Icons.Default.CloudDone,
                    contentDescription = null,
                    modifier = Modifier.padding(7.dp),
                    tint = if (isError) Color(0xFFAD3427) else AccountGreen,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(message, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = if (isError) Color(0xFF8E2A20) else Color(0xFF075F3D))
        }
    }
}

@Composable
private fun AccountField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        colors = accountTextFieldColors(),
        shape = RoundedCornerShape(16.dp),
    )
}

@Composable
private fun accountTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccountGreen,
    focusedLabelColor = AccountGreen,
    cursorColor = AccountGreen,
)

@Composable
private fun GoogleButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = AccountNavy, disabledContainerColor = Color(0xFFF2F3F2)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6DDD5)),
    ) {
        Text("G", style = MaterialTheme.typography.titleLarge, color = Color(0xFF4285F4), fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.width(12.dp))
        Text("Continue with Google", fontWeight = FontWeight.ExtraBold)
    }
}
