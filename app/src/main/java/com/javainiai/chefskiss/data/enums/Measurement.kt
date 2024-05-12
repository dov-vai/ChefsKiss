package com.javainiai.chefskiss.data.enums

/**
 * @param unit solid units should be represented in grams, and liquid units as milliliters
 * @param metric marks if unit is metric or imperial
 * @param weight marks if it measures weight or volume
 */
enum class Measurement(
    val title: String,
    val unit: Float,
    val system: UnitSystem,
    val weight: Boolean
) {
    Gram("Grams (g)", 1f, UnitSystem.Metric, true),
    Kilogram("Kilograms (kg)", 1000f, UnitSystem.Metric, true),
    Milliliter("Mililiters (ml)", 1f, UnitSystem.Metric, false),
    Liter("Liters (l)", 1000f, UnitSystem.Metric, false),
    Teaspoon("Teaspoons (t)", 1f, UnitSystem.All, false),
    Tablespoon("Tablespoon (T)", 3f, UnitSystem.All, false),
    Cup("Cup (c)", 48f, UnitSystem.All, false),
    Ounce("Ounce (oz)", 1f, UnitSystem.Imperial, true),
    Pound("Pound (lb)", 16f, UnitSystem.Imperial, true)
}