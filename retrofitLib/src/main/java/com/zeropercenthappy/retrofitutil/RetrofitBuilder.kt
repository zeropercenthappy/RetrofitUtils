package com.zeropercenthappy.retrofitutil

import android.content.Context
import android.text.TextUtils
import me.jessyan.progressmanager.ProgressManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitBuilder {
    private var baseUrl: String = ""
    private var handleCookie: Boolean = true
    private var extraParamMap = TreeMap<String, String>()
    private val extraHeaderMap = TreeMap<String, String>()
    private val extraInterceptorList = arrayListOf<Interceptor>()
    private var connectTimeoutMs: Long = 10_000
    private var readTimeoutMs: Long = 10_000
    private var writeTimeoutMs: Long = 10_000
    private val okHttpClientBuilder = ProgressManager.getInstance().with(OkHttpClient.Builder())

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

    fun connectTimeout(ms: Long): RetrofitBuilder {
        this.connectTimeoutMs = ms
        return this
    }

    fun readTimeoutMs(ms: Long): RetrofitBuilder {
        this.readTimeoutMs = ms
        return this
    }

    fun writeTimeoutSec(ms: Long): RetrofitBuilder {
        this.writeTimeoutMs = ms
        return this
    }

    fun okhttpClientBuilderOption(option: (OkHttpClient.Builder) -> Unit): RetrofitBuilder {
        option(okHttpClientBuilder)
        return this
    }

    fun build(context: Context): Retrofit {
        //检测baseUrl
        if (TextUtils.isEmpty(baseUrl)) {
            throw Exception("base url can not be empty")
        } else if (!baseUrl.endsWith("/")) {
            throw Exception("base url must end with /")
        }
        // 默认Interceptor
        val defaultInterceptor = DefaultInterceptor(context.applicationContext, handleCookie,
                extraParamMap, extraHeaderMap)
        extraInterceptorList.add(0, defaultInterceptor)
        // LogInterceptor
        if (RetrofitConfig.DEBUG_MODE) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = RetrofitConfig.LOG_LEVEL
            extraInterceptorList.add(loggingInterceptor)
        }
        // 构造OkHttpClient
        val okHttpClient = okHttpClientBuilder.apply {
            for (interceptor in extraInterceptorList) {
                addInterceptor(interceptor)
            }
            connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
            readTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
            writeTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS)
        }.build()

        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
    }
}