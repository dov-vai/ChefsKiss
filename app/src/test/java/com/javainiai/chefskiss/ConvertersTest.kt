package com.javainiai.chefskiss

import android.net.Uri
import com.javainiai.chefskiss.data.database.Converters
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConvertersTest {
    private lateinit var converters: Converters

    @Before
    fun setUp() {
        converters = Converters()
    }

    @Test
    fun testFromString() {
        val uriString = "https://www.example.com"
        val expectedUri = mockk<Uri>()

        mockkStatic(Uri::class)
        every { Uri.parse(uriString) } returns expectedUri

        val result = converters.fromString(uriString)

        assertEquals(expectedUri, result)
    }

    @Test
    fun testFromStringNull() {
        val result = converters.fromString(null)

        assertEquals(null, result)
    }

    @Test
    fun testToString() {
        val uriString = "https://www.example.com"
        val uri = mockk<Uri>()

        every { uri.toString() } returns uriString

        val result = converters.toString(uri)

        assertEquals(uriString, result)
    }

    @Test
    fun testToStringNull() {
        val result = converters.toString(null)

        assertEquals(null, result)
    }
}