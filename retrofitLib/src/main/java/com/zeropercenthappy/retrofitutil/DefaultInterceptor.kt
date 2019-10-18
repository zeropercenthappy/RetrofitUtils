package com.zeropercenthappy.retrofitutil

import android.content.Context
import android.text.TextUtils
import okhttp3.*

class DefaultInterceptor(
    private var context: Context, private val handleCookie: Boolean = true,
    private val extraParamMap: Map<String, String>,
    private val extraHeaderMap: Map<String, String>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        //其它额外参数
        if (extraParamMap.isNotEmpty()) {
            request = addExtraParam(request)
        }
        val requestBuilder = request.newBuilder()
        //是否需要自动管理Cookie
        if (handleCookie) {
            val cookie = CookieManager.getCookie(context, request.url.scheme, request.url.host, request.url.port)
            if (!TextUtils.isEmpty(cookie)) {
                requestBuilder.addHeader(CookieManager.CONSTANT_COOKIE, cookie)
            }
        }
        //其它自定义Header
        for (key in extraHeaderMap.keys) {
            val value = extraHeaderMap[key]
            if (value != null && value != "") {
                requestBuilder.addHeader(key, value)
            }
        }

        val response = chain.proceed(requestBuilder.build())
        //处理返回的Cookie
        CookieManager.updateCookie(context, request.url.scheme, request.url.host, request.url.port, response)

        return response
    }

    private fun addExtraParam(request: Request): Request {
        when (request.method) {
            "GET", "get" -> {
                val httpUrlBuilder = request.url.newBuilder()
                for (key in extraParamMap.keys) {
                    val value = extraParamMap[key]
                    if (value != null && value != "") {
                        httpUrlBuilder.addQueryParameter(key, value)
                    }
                }
                val newHttpUrl = httpUrlBuilder.build()
                return request.newBuilder().url(newHttpUrl).build()
            }
            "POST", "post" -> {
                when (val requestBody = request.body) {
                    is FormBody -> {
                        //FormBody
                        val formBodyBuilder = FormBody.Builder()
                        //原参数
                        for (i in 0 until requestBody.size) {
                            formBodyBuilder.addEncoded(requestBody.encodedName(i), requestBody.encodedValue(i))
                        }
                        //新参数
                        for (key in extraParamMap.keys) {
                            val value = extraParamMap[key]
                            if (value != null && value != "") {
                                formBodyBuilder.addEncoded(key, value)
                            }
                        }
                        val formBody = formBodyBuilder.build()
                        return request.newBuilder()
                            .method(request.method, formBody)
                            .build()
                    }
                    is MultipartBody -> {
                        //MultipartBody
                        val builder = MultipartBody.Builder()
                        builder.setType(MultipartBody.FORM)
                        //原参数
                        for (part in requestBody.parts) {
                            builder.addPart(part)
                        }
                        //新参数
                        for (key in extraParamMap.keys) {
                            val value = extraParamMap[key]
                            if (value != null && value != "") {
                                builder.addFormDataPart(key, value)
                            }
                        }
                        val multipartBody = builder.build()
                        return request.newBuilder()
                            .method(request.method, multipartBody)
                            .build()
                    }
                    else -> return request
                }
            }
            else -> return request
        }
    }
}