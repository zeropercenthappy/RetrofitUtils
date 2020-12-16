package com.zeropercenthappy.retrofitutilsample.api

import com.zeropercenthappy.retrofitutilsample.pojo.GetBean
import com.zeropercenthappy.retrofitutilsample.pojo.LoginBean
import com.zeropercenthappy.retrofitutilsample.pojo.PostBean
import com.zeropercenthappy.retrofitutilsample.pojo.UploadBean
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface IKalleApi {

    companion object {
        const val BASE_URL = "http://kalle.nohttp.net/"

        const val LOGIN = "login"
        const val GET = "method/get"
        const val POST = "method/post"
        const val UPLOAD = "upload/form"
        const val POST_JSON = "upload/body/json"
    }

    @FormUrlEncoded
    @POST(LOGIN)
    fun login(@Field("name") name: String, @Field("password") password: String): Call<LoginBean>

    @GET(GET)
    fun get(@Query("name") name: String, @Query("age") age: String): Call<GetBean>

    @POST(POST)
    fun post(@Body formBody: FormBody): Call<PostBean>

    @Multipart
    @POST(UPLOAD)
    fun uploadFile(
            @Part("name") name: RequestBody,
            @Part("age") age: RequestBody,
            @Part filePartList: List<MultipartBody.Part>
    ): Call<UploadBean>

    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>

    @POST(POST_JSON)
    @Headers("Content-Type: application/json")
    fun postJson(@Body requestBody: RequestBody): Call<ResponseBody>
}