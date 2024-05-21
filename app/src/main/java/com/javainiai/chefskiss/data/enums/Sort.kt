package com.javainiai.chefskiss.data.enums

import android.content.Context
import com.javainiai.chefskiss.R

enum class Sort() {
    COOKING_TIME,
    RATING,
    ADDED,
    PORTION;

    fun getTitle(context: Context): String {
        return when (this) {
            COOKING_TIME -> context.getString(R.string.cooking_time)
            RATING -> context.getString(R.string.rating)
            ADDED -> context.getString(R.string.time_added)
            PORTION -> context.getString(R.string.portion)
        }
    }
}