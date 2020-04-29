package com.volio.coloringbook2.interfaces

import com.volio.coloringbook2.model.ColorBookItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APIService {

    @GET("getAllCategory.php")
    fun getCategory(): Call<ResponseBody>
}