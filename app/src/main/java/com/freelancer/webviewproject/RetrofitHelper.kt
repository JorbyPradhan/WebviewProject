package com.freelancer.webviewproject

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    var BASE_URL="http://ls.tyworld-win.top"


    fun getInstance(context:Context): Retrofit {
        val url = if(BuildConfig.TYPE == 1){
            "http://103.155.214.141:8008/"
        }else{
            "http://103.253.13.69:8369/"
        }
        return Retrofit.Builder()
            .baseUrl(url)
            //.baseUrl(AppSharedPreference(context).getServerUrl().toString())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient())
            .build()
    }
    private fun okhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }
}

interface WebSiteLinkApi {

    @GET("users/get_ssl")
    suspend fun getWebsiteUrl(
    ): Response<String>

    @GET("yunxin/v1/get_links")
    suspend fun getLinks():Response<WebSite>
}

data class WebSite(
    val code:Int,
    val msg:String,
    val data :String
)