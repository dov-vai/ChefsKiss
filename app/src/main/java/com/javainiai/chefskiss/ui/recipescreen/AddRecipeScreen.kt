package com.javainiai.chefskiss.ui.recipescreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.navigation.NavigationDestination
import com.javainiai.chefskiss.ui.theme.ChefsKissTheme
import kotlinx.coroutines.launch


@Preview
@Composable
fun AddRecipeScreenPreview() {
    ChefsKissTheme {
        AddRecipeScreen({})
    }
}

object AddRecipeDestination : NavigationDestination {
    override val route = "addrecipe"
}

@Composable
fun AddRecipeScreen(
    navigateBack: () -> Unit,
    viewModel: AddRecipeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var tabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val tabs = listOf("Overview", "Ingredients", "Directions")

    Scaffold(topBar = {
        AddRecipeTopBar(
            onBack = navigateBack,
            onSave = { coroutineScope.launch { if (viewModel.saveToDatabase()) navigateBack() } })
    }) { padding ->
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
                        .fillMaxWidth()
                )

                1 -> RecipeIngredients(
                    ingredient = uiState.ingredient,
                    updateIngredient = viewModel::updateIngredient,
                    ingredients = uiState.ingredients,
                    updateIngredients = viewModel::updateIngredients,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )

                2 -> RecipeDirections(
                    directions = uiState.directions,
                    onDirectionsChange = viewModel::updateDirections)
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
    val galleryLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
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
                    .size(256.dp)
                    .clickable { galleryLauncher.launch("image/*") })
        } else {
            Button(onClick = { galleryLauncher.launch("image/*") }) {
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
fun RecipeIngredients(
    ingredient: IngredientDisplay,
    updateIngredient: (IngredientDisplay) -> Unit,
    ingredients: List<IngredientDisplay>,
    updateIngredients: (List<IngredientDisplay>) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
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
        TextField(
            value = ingredient.units,
            onValueChange = { updateIngredient(ingredient.copy(units = it)) },
            label = { Text(text = "Unit") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                updateIngredients(ingredients + ingredient)
                updateIngredient(IngredientDisplay("", "", ""))
                focusManager.moveFocus(FocusDirection.Next)
            })
        )
        LazyColumn {
            items(items = ingredients) {
                IngredientCard(ingredient = it, onRemove = { updateIngredients(ingredients - it) })
            }
        }
    }
}

@Composable
fun IngredientCard(
    ingredient: IngredientDisplay,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${ingredient.title} ${ingredient.amount} ${ingredient.units}")
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