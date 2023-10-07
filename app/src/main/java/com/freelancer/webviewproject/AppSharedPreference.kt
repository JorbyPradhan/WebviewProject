package com.freelancer.webviewproject

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreference constructor(context: Context) {

    private val fileName = "DATA"
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, text: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, text)
        editor.apply()
    }

    fun save(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY_NAME, value)
        editor.apply()
    }

    fun saveServerUrl(value:String){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString("server_url", value)
        editor.apply()
    }

    fun getServerUrl(): String? {
        return sharedPref.getString("server_url","https://sls012.top");
    }

    fun save(KEY_NAME: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_NAME, status)
        editor.apply()
    }

    fun save(KEY_NAME: String, status: Long) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putLong(KEY_NAME, status)
        editor.apply()
    }


    fun getValueString(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, null)
    }

    fun getValueInt(KEY_NAME: String,defaultValue: Int = 0): Int {
        return sharedPref.getInt(KEY_NAME, defaultValue)
    }

    fun getValueBoolean(KEY_NAME: String, defaultValue: Boolean = false): Boolean {
        return sharedPref.getBoolean(KEY_NAME, defaultValue)
    }

    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }

}