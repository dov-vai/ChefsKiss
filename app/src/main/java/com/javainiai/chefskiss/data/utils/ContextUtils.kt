package com.javainiai.chefskiss.data.utils

import android.content.Context
import android.content.Intent

object ContextUtils {
    fun Context.restart() {
        val packageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)!!
        val componentName = intent.component!!
        val restartIntent = Intent.makeRestartActivityTask(componentName)
        startActivity(restartIntent)
        Runtime.getRuntime().exit(0)
    }
}