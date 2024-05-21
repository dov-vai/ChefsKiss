package com.javainiai.chefskiss.data.utils

import com.javainiai.chefskiss.data.enums.CookingUnit
import com.javainiai.chefskiss.data.enums.UnitSystem
import com.javainiai.chefskiss.data.ingredient.Ingredient
import okhttp3.internal.toImmutableMap

object UnitUtils {
    val UnitMap = mutableMapOf(
        CookingUnit.Gram to CookingUnit.Ounce,
        CookingUnit.Kilogram to CookingUnit.Pound,
        CookingUnit.Milliliter to CookingUnit.FluidOunce,
        CookingUnit.Liter to CookingUnit.Gallon
    ).apply {
        putAll(this.entries.associate { it.value to it.key })
    }.toImmutableMap()

    fun Ingredient.convertUnit(toSystem: UnitSystem): Ingredient {
        if (this.unit.system == toSystem)
            return this

        val convertedUnit = UnitMap[this.unit] ?: return this

        val convertedSize = this.size * this.unit.base / convertedUnit.base

        return this.copy(
            unit = convertedUnit,
            size = convertedSize
        )
    }
}