package com.javainiai.chefskiss

import com.javainiai.chefskiss.data.CalendarUtils.datePlusOffset
import com.javainiai.chefskiss.data.CalendarUtils.getCurrentDate
import com.javainiai.chefskiss.data.CalendarUtils.getDateString
import com.javainiai.chefskiss.data.CalendarUtils.getEndOfWeek
import com.javainiai.chefskiss.data.CalendarUtils.getStartOfWeek
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarUtilsTest {
    @Test
    fun `getDateString returns correct format`() {
        val date = Date()
        val expectedFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        val actualFormat = date.getDateString()
        assertEquals(expectedFormat, actualFormat)
    }

    @Test
    fun `getStartOfWeek returns correct date`() {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val expectedDate = calendar.time
        val actualDate = getStartOfWeek()
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun `getEndOfWeek returns correct date`() {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val expectedDate = calendar.time
        val actualDate = getEndOfWeek()
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun `getCurrentDate returns correct date`() {
        val expectedDate = Calendar.getInstance().time
        val actualDate = getCurrentDate()
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun `datePlusOffset returns correct date`() {
        val date = Date()
        val dayOffset = 5
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, dayOffset)
        val expectedDate = calendar.time
        val actualDate = datePlusOffset(date, dayOffset)
        assertEquals(expectedDate, actualDate)
    }
}