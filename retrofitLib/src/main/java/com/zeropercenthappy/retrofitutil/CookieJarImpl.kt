package com.zeropercenthappy.retrofitutil

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.lang.ref.WeakReference

class CookieJarImpl(context: Context) : CookieJar {

    companion object {
        private const val cookie_jar_sp_name = "okHttpCookieJar";
    }

    private val contextRef = WeakReference(context.applicationContext)

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieList = arrayListOf<Cookie>()
        val context = contextRef.get() ?: return cookieList
        val protocol = url.scheme
        val domain = url.host
        val port = url.port

        val cookiesStr = context.getSharedPreferences(cookie_jar_sp_name, Context.MODE_PRIVATE)
            .getString("${protocol}_${domain}_${port}", null) ?: return cookieList

        for (cookieStr in cookiesStr.split("$#")) {
            Cookie.parse(url, cookieStr)?.run {
                cookieList.add(this)
            }
        }
        return cookieList
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val context = contextRef.get() ?: return
        if (cookies.isEmpty()) return

        val protocol = url.scheme
        val domain = url.host
        val port = url.port
        val cookiesSB = StringBuilder()

        context.getSharedPreferences(cookie_jar_sp_name, Context.MODE_PRIVATE).edit().run {
            for (cookie in cookies) {
                cookiesSB.append(cookie.toString()).append("&#")
            }
            putString("${protocol}_${domain}_${port}", cookiesSB.toString())
            apply()
        }
    }
}