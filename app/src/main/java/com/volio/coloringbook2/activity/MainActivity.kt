package com.volio.coloringbook2.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adconfigonline.AdHolderOnline
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.volio.coloringbook2.R
import com.volio.coloringbook2.common.gg
import com.volio.coloringbook2.database.config
import com.volio.coloringbook2.interfaces.APIService
import com.volio.coloringbook2.java.util.To
import com.volio.coloringbook2.model.ColorBook
import com.volio.coloringbook2.model.ColorBookItem
import com.volio.coloringbook2.viewmodel.ColorBookViewModel
import io.fabric.sdk.android.Fabric
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var category:ArrayList<ColorBookItem> = ArrayList()
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

        loadData()

    }
    private fun loadData() {

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
                    val gson = Gson()
                    category = gson.fromJson(apiJson, ColorBook::class.java)
                    config.category = apiJson


//                    val x = category[2].listType[0].image_type_url
//                    gg("ghghghghghghghvcvcghghh","$x")


                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Vui lòng kiểm tra lại 3G/Wifi", Toast.LENGTH_SHORT).show()
            }
        })
    }
}



