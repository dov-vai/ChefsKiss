package com.javainiai.chefskiss.data.enums

import android.content.Context
import com.javainiai.chefskiss.R

enum class Meal() {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK;

    fun getTitle(context: Context): String {
        return when (this) {
            BREAKFAST -> context.getString(R.string.breakfast)
            LUNCH -> context.getString(R.string.lunch)
            DINNER -> context.getString(R.string.dinner)
            SNACK -> context.getString(R.string.snack)
        }
    }
}