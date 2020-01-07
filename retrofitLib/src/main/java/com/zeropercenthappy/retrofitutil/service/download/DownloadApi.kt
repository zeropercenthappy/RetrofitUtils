package com.zeropercenthappy.retrofitutil.service.download

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface DownloadApi {

    @GET
    suspend fun download(@Url url: String): ResponseBody
}