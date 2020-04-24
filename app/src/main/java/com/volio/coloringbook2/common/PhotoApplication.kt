package com.volio.coloringbook2.common

import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.multidex.MultiDex
import com.jakewharton.threetenabp.AndroidThreeTen
import com.volio.coloringbook2.database.CalendarDatabase
import com.volio.coloringbook2.java.Lo

class PhotoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Lo.setDEBUG(true)
        CalendarDatabase(applicationContext)
        com.volio.coloringbook2.java.PhotorSharePreUtils.init(applicationContext)
        AndroidThreeTen.init(this)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}