package com.code4galaxy.lingoleap.presentation.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code4galaxy.lingoleap.core.mvi.UiEffect
import com.code4galaxy.lingoleap.core.mvi.UiState
import com.code4galaxy.lingoleap.domain.usecase.ObserveUserPreferencesUseCase
import com.code4galaxy.lingoleap.presentation.navigation.LingoRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashState(
    val isLoading: Boolean = true
) : UiState

sealed interface SplashEffect : UiEffect {
    data class Navigate(
        val route: String
    ) : SplashEffect
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val observeUserPreferences: ObserveUserPreferencesUseCase
) : ViewModel() {

    private val _effect =
        Channel<SplashEffect>(Channel.BUFFERED)

    val effect: Flow<SplashEffect> =
        _effect.receiveAsFlow()

    init {
        viewModelScope.launch {

            val preferences =
                observeUserPreferences().first()

            val route = when {

                !preferences.hasCompletedOnboarding ->
                    LingoRoute.Onboarding.path

                preferences.activeLanguagePairId == null ->
                    LingoRoute.LanguagePicker.path

                preferences.dailyGoalLessons == null ->
                    LingoRoute.GoalSetup.path

                else ->
                    LingoRoute.Home.path
            }

            _effect.send(
                SplashEffect.Navigate(route)
            )
        }
    }
}
