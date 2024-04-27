package com.javainiai.chefskiss.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object CalendarUtils {

    fun Date.getDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(this)
    }

    fun String.getDate(): Date? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.parse(this)
    }

    fun getStartOfWeek(): Date {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return calendar.time
    }

    fun getEndOfWeek(): Date {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        return calendar.time
    }


    fun getCurrentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    fun datePlusOffset(date: Date, dayOffset: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.setTime(date)
        calendar.add(Calendar.DATE, dayOffset)
        return calendar.time
    }

    fun getDaysDifference(firstDate: Date, secondDate: Date): Long {
        val diffMillis = Math.abs(secondDate.time - firstDate.time)
        return TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS)
    }
}