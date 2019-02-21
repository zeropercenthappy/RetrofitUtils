package com.zeropercenthappy.retrofitutil

import okhttp3.logging.HttpLoggingInterceptor

object RetrofitConfig {
    /**
     * true or false
     */
    @JvmStatic
    var DEBUG_MODE = false
    /**
     * [HttpLoggingInterceptor.Level]
     */
    @JvmStatic
    var LOG_LEVEL = HttpLoggingInterceptor.Level.BASIC
}