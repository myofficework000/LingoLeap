package com.lingoleap.presentation.feature.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.core.mvi.UiEffect
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.usecase.ObserveUserPreferencesUseCase
import com.lingoleap.presentation.navigation.LingoRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashState(val isLoading: Boolean = true) : UiState
sealed interface SplashEffect : UiEffect { data class Navigate(val route: String) : SplashEffect }

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val observeUserPreferences: ObserveUserPreferencesUseCase,
) : ViewModel() {
    private val _effect = Channel<SplashEffect>(Channel.BUFFERED)
    val effect: Flow<SplashEffect> = _effect.receiveAsFlow()
    init { viewModelScope.launch {
        val preferences = observeUserPreferences().first()
        val route = when {
            !preferences.hasCompletedOnboarding -> LingoRoute.Onboarding.path
            preferences.activeLanguagePairId == null -> LingoRoute.LanguagePicker.path
            else -> LingoRoute.Home.path
        }
        _effect.send(SplashEffect.Navigate(route))
    } }
}

@Composable
fun SplashRoute(onNavigate: (String) -> Unit, viewModel: SplashViewModel = hiltViewModel()) {
    LaunchedEffect(viewModel) { viewModel.effect.collect { effect -> onNavigate((effect as SplashEffect.Navigate).route) } }
    SplashScreen()
}

@Composable
fun SplashScreen() = Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
) { Text("LingoLeap", style = MaterialTheme.typography.headlineLarge); CircularProgressIndicator() }
