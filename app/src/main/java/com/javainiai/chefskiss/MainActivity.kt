package com.javainiai.chefskiss

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.javainiai.chefskiss.ui.app.ChefsKissApp
import com.javainiai.chefskiss.ui.theme.ChefsKissTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChefsKissTheme {
                ChefsKissApp()
            }
        }
    }
}