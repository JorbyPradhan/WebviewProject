package com.freelancer.webviewproject

import android.app.Application
import android.net.ConnectivityManager
import android.os.StrictMode
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MyApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        //isSiteBlocked()
    }

    private fun isSiteBlocked() {
        AppSharedPreference(applicationContext).saveServerUrl("")
        var reachable = false
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        if (netInfo != null && netInfo.isConnected) { //check internet connection
            for (i in 0 until BuildConfig.URL_ARRAY.size) {
                val value: String = BuildConfig.URL_ARRAY[i]
                try {
                    val url = URL(value)
                    val urlc = url.openConnection() as HttpURLConnection
                    urlc.addRequestProperty("Cache-Control", "max-age=0")
                    urlc.connectTimeout = 3000
                    urlc.connect()
                    if (urlc.responseCode == 200) {
                        reachable = true
                    }
                    // also check different code for down or the site is blocked, example
                    if (urlc.responseCode == 521) {
                        // Web server of the site is down
                        reachable = false
                    }
                    urlc.disconnect()
                } catch (e1: MalformedURLException) {
                    e1.printStackTrace()
                    reachable = false
                } catch (e: IOException) {
                    e.printStackTrace()
                    reachable = false
                }
                if (reachable) {
                    Log.d("TASFDASFDSADFASFDS", "isSiteBlocked: ${BuildConfig.URL_ARRAY[i]}")
                    AppSharedPreference(applicationContext).saveServerUrl(BuildConfig.URL_ARRAY[i])
                    break
                }
            }
        }
    }
}