package com.freelancer.webviewproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val buyerRepository: WebSiteRepostory)  : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if(BuildConfig.TYPE == 1){
                    buyerRepository.getWebsite()
                }else buyerRepository.getLinks()

            } catch (e: HttpException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
    val webSite: LiveData<WebSite>
        get() = buyerRepository.buyers
}