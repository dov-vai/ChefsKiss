package com.javainiai.chefskiss.data.enums

enum class TimerPreset(val title: String, val inSeconds: Int) {
    OneMinute("1 minute", 60),
    FiveMinutes("5 minutes", 5 * 60),
    TenMinutes("10 minutes", 10 * 60),
    FifteenMinutes("15 minutes", 15 * 60),
    ThirtyMinutes("30 minutes", 30 * 60),
    OneHour("1 hour", 60 * 60),
    TwoHours("2 hours", 2 * 60 * 60)
}