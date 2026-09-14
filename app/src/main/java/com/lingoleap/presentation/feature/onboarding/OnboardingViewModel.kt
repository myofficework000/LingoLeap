package com.lingoleap.presentation.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiEffect
import com.lingoleap.domain.usecase.CompleteOnboardingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val completeOnboarding: CompleteOnboardingUseCase,
) : ViewModel(){
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()
    private val _effect = Channel<OnboardingEffect>(Channel.BUFFERED)
    val effect: Flow<OnboardingEffect> = _effect.receiveAsFlow()
    fun onEvent(event: OnboardingEvent){
        when(event){
            OnboardingEvent.Continue->{
                _state.update { currentState->
                    currentState.copy(pageIndex = currentState.pageIndex+1)
                }
            }

            OnboardingEvent.Skip -> viewModelScope.launch {
                completeOnboarding()
                _effect.send(OnboardingEffect.Finished)
            }
        }
    }
}

sealed interface OnboardingEffect : UiEffect { data object Finished : OnboardingEffect }
