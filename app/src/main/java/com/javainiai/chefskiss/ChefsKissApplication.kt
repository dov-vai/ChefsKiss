package com.javainiai.chefskiss

import android.app.Application
import com.javainiai.chefskiss.data.ChefsKissAppContainer

class ChefsKissApplication : Application() {
    lateinit var container: ChefsKissAppContainer
    override fun onCreate() {
        super.onCreate()
        container = ChefsKissAppContainer(this)
    }
}