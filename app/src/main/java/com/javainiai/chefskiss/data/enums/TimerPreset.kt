package com.javainiai.chefskiss.data.enums

import android.content.Context
import com.javainiai.chefskiss.R

enum class TimerPreset(val inSeconds: Int) {
    OneMinute(60),
    FiveMinutes(5 * 60),
    TenMinutes(10 * 60),
    FifteenMinutes(15 * 60),
    ThirtyMinutes(30 * 60),
    OneHour(60 * 60),
    TwoHours(2 * 60 * 60);

    fun getTitle(context: Context): String {
        return when (this) {
            OneMinute -> context.getString(R.string._1_minute)
            FiveMinutes -> context.getString(R.string._5_minutes)
            TenMinutes -> context.getString(R.string._10_minutes)
            FifteenMinutes -> context.getString(R.string._15_minutes)
            ThirtyMinutes -> context.getString(R.string._30_minutes)
            OneHour -> context.getString(R.string._1_hour)
            TwoHours -> context.getString(R.string._2_hours)
        }
    }
}