package com.javainiai.chefskiss.ui.timer

import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.javainiai.chefskiss.R
import com.javainiai.chefskiss.data.enums.TimerPreset
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.components.picker.Picker
import com.javainiai.chefskiss.ui.navigation.NavigationDestination

object TimerDestination : NavigationDestination {
    override val route = "timer"
    const val timerRecipeIdArg = "timerRecipeId"
    val routeWithArgs = "$route/{$timerRecipeIdArg}"
}


@Composable
fun TimerScreen(
    navigateBack: () -> Unit,
    viewModel: TimerViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    val timerIntent = Intent(AlarmClock.ACTION_SET_TIMER)
        .putExtra(AlarmClock.EXTRA_SKIP_UI, true)

    val context = LocalContext.current

    Scaffold(topBar = {
        TimerTopBar(onBack = navigateBack)
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            TextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                placeholder = {
                    Text(text = stringResource(R.string.timerTitle))
                },
                shape = RoundedCornerShape(32.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    IconButton(onClick = { viewModel.updateTitle("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear)
                        )
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.weight(0.3f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.hours))
                    Picker(
                        state = viewModel.hoursPickerState,
                        items = viewModel.hours,
                        textStyle = TextStyle(fontSize = 32.sp)
                    )
                }
                Column(
                    modifier = Modifier.weight(0.3f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.minutes))
                    Picker(
                        state = viewModel.minutesPickerState,
                        items = viewModel.minutesSeconds,
                        textStyle = TextStyle(fontSize = 32.sp)
                    )
                }
                Column(
                    modifier = Modifier.weight(0.3f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.seconds))
                    Picker(
                        state = viewModel.secondsPickerState,
                        items = viewModel.minutesSeconds,
                        textStyle = TextStyle(fontSize = 32.sp)
                    )
                }
            }
            TimerPresets(onTimerPreset = viewModel::setTimeByPreset)
            Spacer(modifier = Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 64.dp)
            ) {
                FilledTonalButton(onClick = {
                    val time = viewModel.hoursPickerState.selectedItem.toInt() * 3600 +
                            viewModel.minutesPickerState.selectedItem.toInt() * 60 +
                            viewModel.secondsPickerState.selectedItem.toInt()
                    context.startActivity(
                        timerIntent
                            .putExtra(AlarmClock.EXTRA_MESSAGE, uiState.title)
                            .putExtra(AlarmClock.EXTRA_LENGTH, time)
                    )
                    navigateBack()
                }
                ) {
                    Text(text = stringResource(R.string.start), style = TextStyle(fontSize = 32.sp))
                }
                FilledTonalButton(
                    onClick = viewModel::resetTimer,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(text = stringResource(R.string.reset), style = TextStyle(fontSize = 32.sp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerTopBar(modifier: Modifier = Modifier, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(R.string.setTimer)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        })
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimerPresets(modifier: Modifier = Modifier, onTimerPreset: (TimerPreset) -> Unit) {
    val context = LocalContext.current
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TimerPreset.entries.forEach { preset ->
            SuggestionChip(
                onClick = { onTimerPreset(preset) },
                label = { Text(text = preset.getTitle(context)) })
        }
    }
}