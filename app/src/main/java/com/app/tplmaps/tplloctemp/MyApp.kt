package com.app.tplmaps.tplloctemp

import android.app.Application
import android.content.Context
import com.cioccarellia.ksprefs.KsPrefs

/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 05/10/2023
 */
class MyApp: Application() {

    companion object {
        lateinit var appContext: Context
        val prefs by lazy { KsPrefs(appContext) }
    }
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}