package com.javainiai.chefskiss.data.enums

/**
 * @param base solid units should be represented in grams, and liquid units as milliliters
 * @param metric marks if unit is metric or imperial
 * @param weight marks if it measures weight or volume
 */
enum class CookingUnit(
    val title: String,
    val base: Float,
    val system: UnitSystem,
    val weight: Boolean
) {
    Gram("Grams (g)", 1f, UnitSystem.Metric, true),
    Kilogram("Kilograms (kg)", 1000f, UnitSystem.Metric, true),
    Milliliter("Mililiters (ml)", 1f, UnitSystem.Metric, false),
    Liter("Liters (l)", 1000f, UnitSystem.Metric, false),
    Teaspoon("Teaspoons (t)", 5f, UnitSystem.All, false),
    Tablespoon("Tablespoons (T)", 15f, UnitSystem.All, false),
    Cup("Cups (c)", 240f, UnitSystem.All, false),
    Ounce("Ounces (oz)", 28f, UnitSystem.Imperial, true),
    FluidOunce("Fluid ounces (floz)", 30f, UnitSystem.Imperial, false),
    Pound("Pounds (lb)", 454f, UnitSystem.Imperial, true),
    Gallon("Gallons (gal)", 3800f, UnitSystem.Imperial, false),
    ToTaste("To taste", 0f, UnitSystem.All, false),
    Pinch("Pinches", 0f, UnitSystem.All, true)
}