package com.lingoleap.presentation.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.lingoleap.domain.usecase.ClearUserPreferencesUseCase

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val clearUserPreferences: ClearUserPreferencesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>(Channel.BUFFERED)
    val effect: Flow<ProfileEffect> = _effect.receiveAsFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {

            ProfileEvent.OpenLanguages -> {
                sendEffect(
                    ProfileEffect.NavigateToLanguages
                )
            }

            ProfileEvent.OpenStatistics -> {
                sendEffect(
                    ProfileEffect.NavigateToStatistics
                )
            }

            ProfileEvent.OpenAchievements -> {
                sendEffect(
                    ProfileEffect.NavigateToAchievements
                )
            }

            ProfileEvent.OpenSettings -> {
                sendEffect(
                    ProfileEffect.NavigateToSettings
                )
            }

            ProfileEvent.OpenHelpSupport -> {
                sendEffect(
                    ProfileEffect.NavigateToHelpSupport
                )
            }

            ProfileEvent.SignOut -> {
                viewModelScope.launch {
                    clearUserPreferences()
                    _effect.send(ProfileEffect.SignOut)
                }
            }
        }


    }

    private fun loadProfile() {
        _state.update { currentState ->
            currentState.copy(
                displayName = "Learner",
                selectedCourseCount = 1
            )
        }
    }

    private fun sendEffect(
        effect: ProfileEffect
    ) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
