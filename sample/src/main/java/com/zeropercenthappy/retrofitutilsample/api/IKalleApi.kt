package com.zeropercenthappy.retrofitutilsample.api

import com.zeropercenthappy.retrofitutilsample.pojo.*
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface IKalleApi {

    @FormUrlEncoded
    @POST(KalleUrl.LOGIN)
    fun login(@Field("name") name: String, @Field("password") password: String): Call<LoginBean>

    @GET(KalleUrl.GET)
    fun get(@Query("name") name: String, @Query("age") age: String): Call<GetBean>

//    @POST(KalleUrl.POST)
//    @FormUrlEncoded
//    fun post(@Field("age") age: String): Call<PostBean>

    @POST(KalleUrl.POST)
    fun post(@Body formBody: FormBody): Call<PostBean>

    @Multipart
    @POST(KalleUrl.UPLOAD)
    fun uploadFile(@Part("name") name: RequestBody, @Part("age") age: RequestBody, @Part partList: List<MultipartBody.Part>): Call<UploadBean>

    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>

    @POST(KalleUrl.POST_JSON)
    fun postJson(@Body simpleBean: SimpleBean): Call<ResponseBody>
}