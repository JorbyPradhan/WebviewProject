package com.freelancer.webviewproject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson

class WebSiteRepostory(
    private val webSiteLinkApi: WebSiteLinkApi
) {

    private val webSiteLiveData = MutableLiveData<WebSite>()
    val buyers: LiveData<WebSite>
        get() = webSiteLiveData
    suspend fun getWebsite(){
        val result = webSiteLinkApi.getWebsiteUrl()
        Log.d("TASDFAFDASFDA", "getWebsite: $result")
        if (result.body() != null){

            val decrypt = EncryptionUtils.decrypt(result.body().toString())
            val m = Gson().fromJson(decrypt, WebSite::class.java)
            webSiteLiveData.postValue(m)
        }
    }

    suspend fun getLinks(){
        val result = webSiteLinkApi.getLinks()
        Log.d("TASDFAFDASFDA", "getLinks: $result")
        if (result.body() != null){
            webSiteLiveData.postValue(result.body())
        }
    }

}