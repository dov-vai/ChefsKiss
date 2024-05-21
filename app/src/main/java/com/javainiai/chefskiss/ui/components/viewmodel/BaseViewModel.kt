package com.javainiai.chefskiss.ui.components.viewmodel

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseViewModel() : ViewModel() {
    val snackbarHostState = SnackbarHostState()

    var messageInProgress: Job? = null
        private set

    protected fun showMessage(message: String) {
        // cancel in case it hasn't finished so the message can be shown immediately
        messageInProgress?.cancel()
        messageInProgress = viewModelScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
}