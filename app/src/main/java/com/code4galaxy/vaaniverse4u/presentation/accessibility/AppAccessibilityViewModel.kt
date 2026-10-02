package com.code4galaxy.vaaniverse4u.presentation.accessibility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code4galaxy.vaaniverse4u.domain.usecase.ObserveUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AppAccessibilityState(
    val textScale: Float = 1f,
    val highContrastEnabled: Boolean = false,
)

/** Keeps app-wide presentation accessibility settings alive across destination changes. */
@HiltViewModel
class AppAccessibilityViewModel @Inject constructor(
    observeUserPreferences: ObserveUserPreferencesUseCase,
) : ViewModel() {
    val state = observeUserPreferences()
        .map { preferences ->
            AppAccessibilityState(
                textScale = preferences.textScale,
                highContrastEnabled = preferences.highContrastEnabled,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppAccessibilityState())
}
