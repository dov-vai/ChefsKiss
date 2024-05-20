package com.javainiai.chefskiss.ui.recipescreen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.javainiai.chefskiss.data.enums.CookingUnit
import com.javainiai.chefskiss.data.enums.UnitSystem
import com.javainiai.chefskiss.data.tag.Tag
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object AddRecipeDestination : NavigationDestination {
    override val route = "addrecipe"
}

object EditRecipeDestination : NavigationDestination {
    override val route = "editrecipe"
    const val editRecipeIdArg = "editRecipeId"
    val routeWithArgs = "$route/{${editRecipeIdArg}}"
}

@Composable
fun AddRecipeScreen(
    navigateBack: () -> Unit,
    viewModel: AddRecipeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val tags by viewModel.tags.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var tabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val tabs = listOf("Overview", "Tags", "Ingredients", "Directions")

    Scaffold(topBar = {
        AddRecipeTopBar(
            onBack = navigateBack,
            onSave = { coroutineScope.launch { if (viewModel.saveToDatabase()) navigateBack() } })
    },
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(text = label) })
                }
            }
            when (tabIndex) {
                0 -> RecipeOverview(
                    selectedImage = uiState.imageUri,
                    updateSelectedImage = viewModel::updateImageUri,
                    title = uiState.title,
                    onTitleChange = viewModel::updateTitle,
                    cookingTime = uiState.cookingTime,
                    onCookingTimeChange = viewModel::updateCookingTime,
                    servings = uiState.servings,
                    onServingsChange = viewModel::updateServings,
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                )

                1 -> RecipeTags(
                    tag = uiState.tag,
                    onTagChange = viewModel::updateTag,
                    onAddTag = viewModel::addTag,
                    onRemoveTag = viewModel::removeTag,
                    tags = tags,
                    selectedTags = uiState.tags,
                    updateTags = viewModel::updateTags,
                    tagRemoveMode = uiState.tagRemoveMode,
                    updateMode = viewModel::updateTagRemoveMode,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )

                2 -> RecipeIngredients(
                    ingredient = uiState.ingredient,
                    updateIngredient = viewModel::updateIngredient,
                    editingIngredient = uiState.editingIngredient,
                    updateEditingIngredient = viewModel::updateEditingIngredient,
                    ingredients = uiState.ingredients,
                    updateIngredients = viewModel::updateIngredients,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )

                3 -> RecipeDirections(
                    directions = uiState.directions,
                    onDirectionsChange = viewModel::updateDirections
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingTimePicker(onCookingTimeChange: (String) -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }
    val state = rememberTimePickerState(is24Hour = true)

    Box(propagateMinConstraints = false) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = { showTimePicker = true }
        ) {
            Text("Set Time")
        }
    }
    if (showTimePicker) {
        TimePickerDialog(
            onCancel = { showTimePicker = false },
            onConfirm = {
                val hour = if (state.hour == 24) 0 else state.hour
                val minutes = hour * 60 + state.minute
                onCookingTimeChange(minutes.toString())
                showTimePicker = false
            },
        ) {
            TimePicker(state = state)
        }
    }
}


@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("Confirm") }
                }
            }
        }
    }
}


@Composable
fun RecipeOverview(
    selectedImage: Uri,
    updateSelectedImage: (Uri) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    cookingTime: String,
    onCookingTimeChange: (String) -> Unit,
    servings: String,
    onServingsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // for blocking the launcher from opening repeatedly if the user clicks the button fast multiple times
    var canSelectPhoto by remember { mutableStateOf(true) }
    val photoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) {
            canSelectPhoto = true
            it?.let { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                updateSelectedImage(uri)
            }
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (selectedImage != Uri.EMPTY) {
            AsyncImage(
                model = selectedImage,
                contentDescription = "Selected image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(128.dp)
                    .clickable {
                        if (canSelectPhoto) {
                            photoPickerLauncher.launch(arrayOf("image/*"))
                            canSelectPhoto = false
                        }

                    })
        } else {
            Button(onClick = {
                if (canSelectPhoto) {
                    photoPickerLauncher.launch(arrayOf("image/*"))
                    canSelectPhoto = false
                }
            }) {
                Text(text = "Pick image from gallery")
            }
        }

        TextField(
            value = title,
            onValueChange = { onTitleChange(it) },
            label = { Text(text = "Title") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Title,
                    contentDescription = "Title"
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        TextField(
            value = cookingTime,
            onValueChange = { onCookingTimeChange(it) },
            label = { Text(text = "Cooking Time (minutes)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Cooking time"
                )
            }
        )

        CookingTimePicker(onCookingTimeChange)

        TextField(
            value = servings,
            onValueChange = { onServingsChange(it) },
            label = { Text(text = "Servings") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "Servings"
                )
            }
        )

    }
}

@Composable
fun RecipeTags(
    tag: String,
    onTagChange: (String) -> Unit,
    onAddTag: () -> Unit,
    onRemoveTag: (Tag) -> Unit,
    tags: List<Tag>,
    selectedTags: List<Tag>,
    updateTags: (List<Tag>) -> Unit,
    tagRemoveMode: Boolean,
    updateMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = tag,
                onValueChange = { onTagChange(it) },
                label = { Text(text = "Add new tag") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onAddTag()
                        KeyboardActions.Default.onDone
                    }
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = "Tag"
                    )
                }
            )
            IconButton(
                onClick = { updateMode(!tagRemoveMode) },
                colors = IconButtonDefaults.iconButtonColors(containerColor = if (tagRemoveMode) Color.Red else Color.Transparent)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete tags")
            }
        }
        RecipeTagsCard(
            tags = tags,
            selectedTags = selectedTags,
            updateTags = updateTags,
            tagRemoveMode = tagRemoveMode,
            onRemoveTag = onRemoveTag,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        )
    }

}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeTagsCard(
    tags: List<Tag>,
    selectedTags: List<Tag>,
    updateTags: (List<Tag>) -> Unit,
    tagRemoveMode: Boolean,
    onRemoveTag: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tags.forEach {
            FilterChip(
                selected = selectedTags.contains(it),
                onClick = {
                    if (tagRemoveMode)
                        onRemoveTag(it)
                    else {
                        if (selectedTags.contains(it))
                            updateTags(selectedTags - it)
                        else
                            updateTags(selectedTags + it)
                    }
                },
                label = { Text(it.title) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeIngredients(
    ingredient: IngredientDisplay,
    updateIngredient: (IngredientDisplay) -> Unit,
    editingIngredient: IngredientDisplay?,
    updateEditingIngredient: (IngredientDisplay?) -> Unit,
    ingredients: List<IngredientDisplay>,
    updateIngredients: (List<IngredientDisplay>) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var imperialSelected by remember { mutableStateOf(false) }
    var weightSelected by remember { mutableStateOf(false) }
    var volumeSelected by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = ingredient.title,
            onValueChange = { updateIngredient(ingredient.copy(title = it)) },
            label = { Text(text = "Ingredient") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        TextField(
            value = ingredient.amount,
            onValueChange = { updateIngredient(ingredient.copy(amount = it)) },
            label = { Text(text = "Amount") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded }
        ) {
            TextField(
                value = ingredient.units.title,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    FilterChip(
                        selected = imperialSelected,
                        onClick = { imperialSelected = !imperialSelected },
                        label = { Text(text = "Imperial") })
                    FilterChip(
                        selected = weightSelected,
                        onClick = { weightSelected = !weightSelected },
                        label = { Text(text = "Weight") })
                    FilterChip(
                        selected = volumeSelected,
                        onClick = { volumeSelected = !volumeSelected },
                        label = { Text(text = "Volume") })
                }
                Column(
                    modifier = Modifier
                        .height(256.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val system = if (imperialSelected) UnitSystem.Imperial else UnitSystem.Metric
                    CookingUnit.entries.filter {
                        if (weightSelected && volumeSelected) {
                            it.system == system || it.system == UnitSystem.All
                        } else if (weightSelected) {
                            (it.system == system || it.system == UnitSystem.All) && it.weight
                        } else if (volumeSelected) {
                            (it.system == system || it.system == UnitSystem.All) && !it.weight
                        } else {
                            (it.system == system || it.system == UnitSystem.All)
                        }
                    }.sortedBy { it.title }.forEach { measurement ->
                        DropdownMenuItem(
                            text = { Text(text = measurement.title) },
                            onClick = {
                                updateIngredient(ingredient.copy(units = measurement))
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        FilledTonalButton(onClick = {
            if (editingIngredient != null) {
                updateIngredients(ingredients.map { i ->
                    if (i == editingIngredient) ingredient else i
                })
                updateEditingIngredient(null)
            } else {
                updateIngredients(ingredients + ingredient)
            }
            updateIngredient(IngredientDisplay("", "", CookingUnit.Gram))
        }) {
            Text(text = "Add ingredient")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items = ingredients) {
                IngredientCard(
                    ingredient = it,
                    onRemove = { updateIngredients(ingredients - it) },
                    onEdit = {
                        updateEditingIngredient(it)
                        updateIngredient(it)
                    })
            }
        }
    }
}

@Composable
fun IngredientCard(
    ingredient: IngredientDisplay,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
    containerColor: Color = CardDefaults.cardColors().containerColor,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = containerColor)) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = ingredient.title,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(text = ingredient.amount)
            Text(
                text = ingredient.units.title,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Remove")
            }
        }
    }
}

@Composable
fun RecipeDirections(
    directions: String,
    onDirectionsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = directions,
        onValueChange = { onDirectionsChange(it) },
        modifier.fillMaxSize(),
        label = {
            Text(
                text = "Enter cooking directions"
            )
        })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeTopBar(onBack: () -> Unit, onSave: () -> Unit) {
    TopAppBar(title = {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onSave) {
                Icon(imageVector = Icons.Default.Done, contentDescription = "Done")
            }
        }
    },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}