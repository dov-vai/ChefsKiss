package com.javainiai.chefskiss.data.enums

/**
 * @param unit solid units should be represented in grams, and liquid units as milliliters
 * @param metric marks if unit is metric or imperial
 * @param weight marks if it measures weight or volume
 */
enum class Measurement(
    val title: String,
    val unit: Float,
    val metric: Boolean,
    val weight: Boolean
) {
    Gram("Grams (g)", 1f, true, true),
    Kilogram("Kilograms (kg)", 1000f, true, true),
    Milliliter("Mililiters (ml)", 1f, true, false),
    Liter("Liters (l)", 1000f, true, false),
    Teaspoon("Teaspoons (t)", 1f, false, false),
    Tablespoon("Tablespoon (T)", 3f, false, false),
    Cup("Cup (c)", 48f, false, false),
    Ounce("Ounce (oz)", 1f, false, true),
    Pound("Pound (lb)", 16f, false, true)
}