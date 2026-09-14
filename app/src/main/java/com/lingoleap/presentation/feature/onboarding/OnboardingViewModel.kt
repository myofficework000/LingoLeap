package com.lingoleap.presentation.feature.onboarding

import androidx.lifecycle.ViewModel
import com.lingoleap.core.mvi.UiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(): ViewModel(){
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()
    fun onEvent(event: OnboardingEvent){
        when(event){
            OnboardingEvent.Continue->{
                _state.update { currentState->
                    currentState.copy(pageIndex = currentState.pageIndex+1)
                }
            }

            else -> {}
        }
    }
}