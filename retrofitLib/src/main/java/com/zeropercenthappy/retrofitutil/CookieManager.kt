package com.zeropercenthappy.retrofitutil

import android.content.Context
import android.text.TextUtils
import retrofit2.Response

object CookieManager {
    const val CONSTANT_SET_COOKIE = "Set-Cookie"
    const val CONSTANT_COOKIE = "Cookie"

    @JvmStatic
    fun getCookie(context: Context): String {
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).run {
            return getString(CONSTANT_COOKIE, "") ?: ""
        }
    }

    @JvmStatic
    private fun setCookie(context: Context, cookie: String) {
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).edit().run {
            putString(CONSTANT_COOKIE, cookie)
            apply()
        }
    }

    @JvmStatic
    fun updateCookie(context: Context, response: Response<*>) {
        response.headers()?.get(CONSTANT_SET_COOKIE)?.run {
            if (!TextUtils.isEmpty(this)) {
                setCookie(context, this)
            }
        }
    }

    @JvmStatic
    fun updateCookie(context: Context, response: okhttp3.Response) {
        response.headers[CONSTANT_SET_COOKIE]?.run {
            if (!TextUtils.isEmpty(this)) {
                setCookie(context, this)
            }
        }
    }

    @JvmStatic
    fun updateCookie(context: Context, cookie: String) {
        if (TextUtils.isEmpty(cookie)) {
            setCookie(context, cookie)
        }
    }
}