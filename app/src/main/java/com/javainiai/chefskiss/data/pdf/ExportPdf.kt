package com.javainiai.chefskiss.data.pdf

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import com.javainiai.chefskiss.data.database.ingredient.Ingredient
import com.javainiai.chefskiss.data.database.recipe.Recipe

fun getRecipeCardHtml(recipe: Recipe, ingredients: List<Ingredient>): String {
    var rating = ""
    repeat(recipe.rating) {
        rating += "★"
    }
    repeat(5 - recipe.rating) {
        rating += "☆"
    }

    var ingredientHtml = ""
    for (ingredient in ingredients) {
        ingredientHtml += "<li>${ingredient.name} ${if (ingredient.size != 0f) ingredient.size else ""} ${ingredient.unit}</li>\n"
    }

    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Recipe Card</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 0;
            }
            .recipe-card {
                max-width: 600px;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                margin: 20px auto;
                padding: 20px;
            }
            .recipe-title {
                display: flex;
                align-items: center;
                margin-bottom: 20px;
            }
            .recipe-title img {
                width: 128px; /* Adjust image size as needed */
                height: 128px;
                border-radius: 30%;
                margin-right: 20px;
                word-wrap: break-word;
            }
            .recipe-details {
                flex: 1;
            }
            h2, .details {
                color: #333;
                margin: 0;
            }
            .details span {
                display: block;
                margin-bottom: 5px;
            }
            ul, ol {
                list-style-type: none;
                padding: 0;
            }
            li {
                margin-bottom: 5px;
                word-wrap: break-word;
            }
            p {
                white-space: pre-line;
                word-wrap: break-word;
            }
        </style>
        </head>
        <body>
        
        <div class="recipe-card">
            <div class="recipe-title">
                <img src="${recipe.imagePath}" alt="Recipe Image">
                <div class="recipe-details">
                    <h2>${recipe.title}</h2>
                    <div class="details">
                        <span>Servings: ${recipe.servings}</span>
                        <span>Cooking Time: ${recipe.cookingTime} minutes</span>
                        <span>Rating: $rating</span>
                    </div>
                </div>
            </div>
            <div>
                <div>
                    <h3>Ingredients:</h3>
                    <ul>
                        $ingredientHtml
                    </ul>
                </div>
                <div>
                    <h3>Instructions:</h3>
                    <p>${recipe.description}</p>
                </div>
            </div>
        </div>
        
        </body>
        </html>
    """
}

fun exportAsPdf(webView: WebView?, context: Context, documentName: String) {
    if (webView != null) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = webView.createPrintDocumentAdapter(documentName.ifEmpty { "recipe" })
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .build()
        printManager.print(
            "ExportPDF",
            printAdapter,
            printAttributes
        )
    }
}