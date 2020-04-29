package com.volio.coloringbook2.database

import android.content.Context

const val PREFS_KEY = "Prefs"

class Configs(context: Context)  {

    protected val prefs = context.getSharedPrefs()
    companion object {
        fun newInstance(context: Context) = Configs(context)
    }
    var category: String?
        get() = prefs.getString("category", "")
        set(category) = prefs.edit().putString("category", category).apply()
}
fun Context.getSharedPrefs() = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
val Context.config: Configs get() = Configs.newInstance(applicationContext)