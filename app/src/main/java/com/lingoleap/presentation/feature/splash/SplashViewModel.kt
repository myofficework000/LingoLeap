package com.lingoleap.presentation.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.usecase.GetSelectedLanguagePairUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getSelectedLanguagePairUseCase: GetSelectedLanguagePairUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow<SplashState>(
            SplashState.Loading
        )

    val state: StateFlow<SplashState> =
        _state.asStateFlow()

    private val _effect =
        Channel<SplashEffect>(
            capacity = Channel.BUFFERED
        )

    val effect =
        _effect.receiveAsFlow()

    init {
        resolveStartDestination()
    }

    private fun resolveStartDestination() {

        viewModelScope.launch {

            _state.value =
                SplashState.Loading

            try {

                val preferences =
                    getSelectedLanguagePairUseCase()
                        .first()

                val effect = when {

                    !preferences.onboardingCompleted -> {
                        SplashEffect.NavigateToOnboarding
                    }

                    preferences.sourceLanguageId.isNullOrBlank() ||
                            preferences.targetLanguageId.isNullOrBlank() ||
                            preferences.activeLanguagePairId.isNullOrBlank() -> {

                        SplashEffect.NavigateToLanguagePicker
                    }

                    else -> {
                        SplashEffect.NavigateToHome
                    }
                }

                _state.value =
                    SplashState.Success

                _effect.send(effect)

            } catch (e: Exception) {

                _state.value =
                    SplashState.Error(
                        message = e.message
                            ?: "Unable to load preferences"
                    )
            }
        }
    }
}