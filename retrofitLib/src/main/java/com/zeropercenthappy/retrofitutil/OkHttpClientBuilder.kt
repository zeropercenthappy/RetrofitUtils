package com.zeropercenthappy.retrofitutil

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class OkHttpClientBuilder {
    private val interceptorList = arrayListOf<Interceptor>()

    fun addInterceptor(interceptor: Interceptor): OkHttpClientBuilder {
        interceptorList.add(interceptor)
        return this
    }

    fun addInterceptors(interceptorList: List<Interceptor>): OkHttpClientBuilder {
        this.interceptorList.addAll(interceptorList)
        return this
    }

    fun build(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        for (interceptor in interceptorList) {
            builder.addInterceptor(interceptor)
        }
        return builder.build()
    }
}