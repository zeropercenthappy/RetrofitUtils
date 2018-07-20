package com.zeropercenthappy.retrofitutil

import okhttp3.logging.HttpLoggingInterceptor

object Config {
    /**
     * true or false
     */
    var DEBUG_MODE = false
    /**
     * [HttpLoggingInterceptor.Level]
     */
    var LOG_LEVEL = HttpLoggingInterceptor.Level.BODY
}