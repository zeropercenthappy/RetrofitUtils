package com.zeropercenthappy.retrofitutil

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class OkHttpClientBuilder {
    private val interceptorList = arrayListOf<Interceptor>()
    private var connectTimeoutMs: Long = 10_000
    private var readTimeoutMs: Long = 10_000
    private var writeTimeoutMs: Long = 10_000

    fun addInterceptor(interceptor: Interceptor): OkHttpClientBuilder {
        interceptorList.add(interceptor)
        return this
    }

    fun addInterceptors(interceptorList: List<Interceptor>): OkHttpClientBuilder {
        this.interceptorList.addAll(interceptorList)
        return this
    }

    fun connectTimeout(ms: Long): OkHttpClientBuilder {
        this.connectTimeoutMs = ms
        return this
    }

    fun readTimeoutMs(ms: Long): OkHttpClientBuilder {
        this.readTimeoutMs = ms
        return this
    }

    fun writeTimeoutSec(ms: Long): OkHttpClientBuilder {
        this.writeTimeoutMs = ms
        return this
    }

    fun build(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        for (interceptor in interceptorList) {
            builder.addInterceptor(interceptor)
        }
        builder.connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
        builder.readTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
        builder.writeTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS)
        return builder.build()
    }
}