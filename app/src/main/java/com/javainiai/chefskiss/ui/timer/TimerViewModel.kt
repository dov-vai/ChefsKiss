package com.javainiai.chefskiss.ui.timer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.enums.TimerPreset
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.ui.components.picker.PickerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerUiState(
    val title: String
)

class TimerViewModel(savedStateHandle: SavedStateHandle, recipesRepository: RecipesRepository) :
    ViewModel() {
    private val recipeId: Long = checkNotNull(savedStateHandle[TimerDestination.timerRecipeIdArg])

    val hours = (0..99).map { it.toString() }
    val minutesSeconds = (0..60).map { it.toString() }

    val hoursPickerState = PickerState()
    val minutesPickerState = PickerState()
    val secondsPickerState = PickerState()

    private var _uiState = MutableStateFlow(TimerUiState(""))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            recipesRepository.getRecipeStream(recipeId).firstOrNull()?.let {
                updateTitle(it.title)
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { currentState ->
            currentState.copy(
                title = title
            )
        }
    }

    fun resetTimer() {
        viewModelScope.launch {
            hoursPickerState.listState.scrollToItem(hoursPickerState.listStartIndex)
            minutesPickerState.listState.scrollToItem(minutesPickerState.listStartIndex)
            secondsPickerState.listState.scrollToItem(secondsPickerState.listStartIndex)
        }

    }

    fun setTimeByPreset(preset: TimerPreset) {
        val hours = preset.inSeconds / 3600
        val minutes = preset.inSeconds % 3600 / 60
        val seconds = preset.inSeconds % 60
        setTimer(hours, minutes, seconds)
    }

    private fun setTimer(hours: Int, minutes: Int, seconds: Int) {
        viewModelScope.launch {
            hoursPickerState.listState.scrollToItem(hoursPickerState.listStartIndex + hours)
            minutesPickerState.listState.scrollToItem(minutesPickerState.listStartIndex + minutes)
            secondsPickerState.listState.scrollToItem(secondsPickerState.listStartIndex + seconds)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}