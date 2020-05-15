package com.volio.coloringbook2.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat.getCategory
import com.adconfigonline.AdHolderOnline
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.gg
import com.volio.coloringbook2.common.toast
import com.volio.coloringbook2.database.config
import com.volio.coloringbook2.interfaces.APIService
import com.volio.coloringbook2.java.util.To
import com.volio.coloringbook2.model.ColorBook
import com.volio.coloringbook2.model.ColorBookItem
import com.volio.coloringbook2.model.storybook.StoryBook
import com.volio.coloringbook2.model.storybook.StoryBookItem
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


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
        getCategory()
        getStoryBook()
        config.Count = 0

    }
    private fun getCategory() {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://mycat.asia/volio_colorbook/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create<APIService>(APIService::class.java).getCategory().enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                var apiJson: String? = null
                try {
                    apiJson = response.body()!!.string()
                    config.category = apiJson
                    gg("huhuhuhuhuhuhuhuhzzzz","$apiJson")


                    config.checkNetwork = true

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Check your connection and try again", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun getStoryBook() {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://mycat.asia/volio_colorbook/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create<APIService>(APIService::class.java).getStoryBook().enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                var apiJson: String? = null
                try {
                    apiJson = response.body()!!.string()
                    config.storyBook = apiJson
                    config.checkNetwork = true

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Check your connection and try again", Toast.LENGTH_SHORT).show()
            }
        })
    }

}



