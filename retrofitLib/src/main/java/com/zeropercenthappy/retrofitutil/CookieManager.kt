package com.zeropercenthappy.retrofitutil

import android.content.Context
import android.text.TextUtils
import retrofit2.Response

object CookieManager {

    private const val CONSTANT_SET_COOKIE = "Set-Cookie"
    const val CONSTANT_COOKIE = "Cookie"

    @JvmStatic
    fun getCookie(context: Context, protocol: String, domain: String, port: Int): String {
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).run {
            return getString("${CONSTANT_COOKIE}_${protocol}_${domain}_${port}", "") ?: ""
        }
    }

    @JvmStatic
    private fun setCookie(context: Context, protocol: String, domain: String, port: Int, cookie: String) {
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).edit().run {
            putString("${CONSTANT_COOKIE}_${protocol}_${domain}_${port}", cookie)
            apply()
        }
    }

    @JvmStatic
    fun updateCookie(context: Context, protocol: String, domain: String, port: Int, response: Response<*>) {
        val cookieSB = StringBuilder()
        for (cookie in response.headers().values(CONSTANT_SET_COOKIE)) {
            cookieSB.append(cookie).append("; ")
        }
        if (cookieSB.isNotEmpty()) {
            setCookie(context, protocol, domain, port, cookieSB.toString())
        }
    }

    @JvmStatic
    fun updateCookie(context: Context, protocol: String, domain: String, port: Int, response: okhttp3.Response) {
        val cookieSB = StringBuilder()
        for (cookie in response.headers(CONSTANT_SET_COOKIE)) {
            cookieSB.append(cookie).append("; ")
        }
        if (cookieSB.isNotEmpty()) {
            setCookie(context, protocol, domain, port, cookieSB.toString())
        }
    }

    @JvmStatic
    fun updateCookie(context: Context, protocol: String, domain: String, port: Int, cookie: String) {
        if (TextUtils.isEmpty(cookie)) {
            setCookie(context, protocol, domain, port, cookie)
        }
    }
}