package com.mufiid.pilwali2020.utils

import android.content.Context

class Constants {
    companion object {
        const val API_ENDPOINT = "http://192.168.1.3/pilwali-2020/index.php/api/"

        fun getUsername(context: Context) : String {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            return pref.getString("USERNAME", "undefined")!!
        }

        fun setUsername(context: Context, username: String) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().apply {
                putString("USERNAME", username)
                apply()
            }
        }

        fun getIDUser(context: Context) : String {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            return pref.getString("ID_USER", "undefined")!!
        }

        fun setIDTps(context: Context, username: String) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().apply {
                putString("ID_TPS", username)
                apply()
            }
        }

        fun getIDTps(context: Context) : String {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            return pref.getString("ID_TPS", "undefined")!!
        }

        fun setIDUser(context: Context, username: String) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().apply {
                putString("ID_USER", username)
                apply()
            }
        }

        fun ISLOGGEDIN(context: Context) : Boolean {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            return pref.getBoolean("ISLOGGEDIN", false)!!
        }

        fun setISLOGGEDIN(context: Context, state: Boolean) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().apply {
                putBoolean("ISLOGGEDIN", state)
                apply()
            }
        }

        fun clear(context: Context) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().clear().apply()
        }

        fun isValidPhone(phone: String) = phone.length >= 12
    }
}