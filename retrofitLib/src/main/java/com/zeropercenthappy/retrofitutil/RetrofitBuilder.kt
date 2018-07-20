package com.zeropercenthappy.retrofitutil

import android.content.Context
import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*

class RetrofitBuilder {
    private var baseUrl: String = ""
    private var handleCookie: Boolean = true
    private var extraParamMap = TreeMap<String, String>()
    private val extraHeaderMap = TreeMap<String, String>()
    private val extraInterceptorList = arrayListOf<Interceptor>()

    fun baseUrl(baseUrl: String): RetrofitBuilder {
        this.baseUrl = baseUrl
        return this
    }

    fun handleCookie(handleCookie: Boolean): RetrofitBuilder {
        this.handleCookie = handleCookie
        return this
    }

    fun addParam(key: String, value: String): RetrofitBuilder {
        extraParamMap[key] = value
        return this
    }

    fun addParams(params: Map<String, String>): RetrofitBuilder {
        for (key in params.keys) {
            val value = params[key]
            if (value != null && value != "") {
                extraParamMap[key] = value
            }
        }
        return this
    }

    fun addHeader(key: String, value: String): RetrofitBuilder {
        extraHeaderMap[key] = value
        return this
    }

    fun addHeaders(headers: Map<String, String>): RetrofitBuilder {
        for (key in headers.keys) {
            val value = headers[key]
            if (value != null && value != "") {
                extraHeaderMap[key] = value
            }
        }
        return this
    }

    fun addInterceptor(interceptor: Interceptor): RetrofitBuilder {
        extraInterceptorList.add(interceptor)
        return this
    }

    fun addInterceptors(interceptorList: List<Interceptor>): RetrofitBuilder {
        this.extraInterceptorList.addAll(interceptorList)
        return this
    }

    fun build(context: Context): Retrofit {
        if (TextUtils.isEmpty(baseUrl)) {
            throw Exception("base url can not be empty")
        }
        // 默认Interceptor
        val defaultInterceptor = DefaultInterceptor(context.applicationContext, handleCookie,
                extraParamMap, extraHeaderMap)
        extraInterceptorList.add(0, defaultInterceptor)
        // LogInterceptor
        if (Config.DEBUG_MODE) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = Config.LOG_LEVEL
            extraInterceptorList.add(loggingInterceptor)
        }
        // 构造OkHttpClient
        val okHttpClient = OkHttpClientBuilder()
                .addInterceptors(extraInterceptorList)
                .build()

        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
    }
}