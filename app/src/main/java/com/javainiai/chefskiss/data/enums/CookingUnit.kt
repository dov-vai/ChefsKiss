package com.javainiai.chefskiss.data.enums

import android.content.Context
import com.javainiai.chefskiss.R

/**
 * @param base solid units should be represented in grams, and liquid units as milliliters
 * @param metric marks if unit is metric or imperial
 * @param weight marks if it measures weight or volume
 */
enum class CookingUnit(
    val base: Float,
    val system: UnitSystem,
    val weight: Boolean
) {
    Gram(1f, UnitSystem.Metric, true),
    Kilogram(1000f, UnitSystem.Metric, true),
    Milliliter(1f, UnitSystem.Metric, false),
    Liter(1000f, UnitSystem.Metric, false),
    Teaspoon(5f, UnitSystem.All, false),
    Tablespoon(15f, UnitSystem.All, false),
    Cup(240f, UnitSystem.All, false),
    Ounce(28f, UnitSystem.Imperial, true),
    FluidOunce(30f, UnitSystem.Imperial, false),
    Pound(454f, UnitSystem.Imperial, true),
    Gallon(3800f, UnitSystem.Imperial, false),
    ToTaste(0f, UnitSystem.All, false),
    Pinch(0f, UnitSystem.All, true);

    fun getTitle(context: Context): String {
        return when (this) {
            Gram -> context.getString(R.string.grams_g)
            Kilogram -> context.getString(R.string.kilograms_kg)
            Milliliter -> context.getString(R.string.mililiters_ml)
            Liter -> context.getString(R.string.liters_l)
            Teaspoon -> context.getString(R.string.teaspoons_t)
            Tablespoon -> context.getString(R.string.tablespoons_t)
            Cup -> context.getString(R.string.cups_c)
            Ounce -> context.getString(R.string.ounces_oz)
            FluidOunce -> context.getString(R.string.fluid_ounces_floz)
            Pound -> context.getString(R.string.pounds_lb)
            Gallon -> context.getString(R.string.gallons_gal)
            ToTaste -> context.getString(R.string.to_taste)
            Pinch -> context.getString(R.string.pinches)
        }
    }
}