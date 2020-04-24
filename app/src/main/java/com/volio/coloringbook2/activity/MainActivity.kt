package com.volio.coloringbook2.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adconfigonline.AdHolderOnline
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.volio.coloringbook2.R
import com.volio.coloringbook2.java.util.To
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.doAsync


class MainActivity : AppCompatActivity() {

    companion object {
        public lateinit var firebaseAnalytics: FirebaseAnalytics
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Recovery.getInstance()
//            .debug(true)
//            .recoverInBackground(false)
//            .recoverStack(true)
//            .mainPage(MainActivity::class.java)
//            .recoverEnabled(true)
//            .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
//            .init(this)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setContentView(R.layout.activity_main)
        AdHolderOnline(this).setDebugMode(true)
        To.init(applicationContext)
//        Fabric.with(this, Crashlytics())
//        val fabric = Fabric.Builder(this)
//            .kits(Crashlytics())
//            .debuggable(true)
//            .build()
//        Fabric.with(fabric)
        doAsync {
            Glide.get(this@MainActivity).clearDiskCache()
        }
    }
}



