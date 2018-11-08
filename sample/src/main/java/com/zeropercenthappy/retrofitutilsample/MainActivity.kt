package com.zeropercenthappy.retrofitutilsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.yanzhenjie.album.Album
import com.zeropercenthappy.retrofitutil.RequestBodyBuilder
import com.zeropercenthappy.retrofitutil.RetrofitBuilder
import com.zeropercenthappy.retrofitutil.RetrofitConfig
import com.zeropercenthappy.retrofitutilsample.api.IKalleApi
import com.zeropercenthappy.retrofitutilsample.api.KalleUrl
import com.zeropercenthappy.retrofitutilsample.pojo.*
import com.zeropercenthappy.utilslibrary.CacheUtils
import com.zeropercenthappy.utilslibrary.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.FormBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var extraTestParamMap: Map<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitConfig.DEBUG_MODE = true
        RetrofitConfig.LOG_LEVEL = HttpLoggingInterceptor.Level.BODY

        extraTestParamMap = mapOf("atopkey" to "aTopValue", "customKey" to "customValue")

        btn_login.setOnClickListener { login() }
        btn_get.setOnClickListener { get() }
        btn_post.setOnClickListener { post() }
        btn_upload.setOnClickListener { pickImage() }
        btn_download.setOnClickListener { download() }
        btn_post_json.setOnClickListener { postJson() }
    }

    private fun login() {
        val retrofit = RetrofitBuilder()
                .baseUrl(KalleUrl.BASE_URL)
                .addParams(extraTestParamMap)
                .build(this)
        val kalleApi = retrofit.create(IKalleApi::class.java)
        val login = kalleApi.login("guest", "123456")
        login.enqueue(object : Callback<LoginBean> {
            override fun onFailure(call: Call<LoginBean>, t: Throwable) {
            }

            override fun onResponse(call: Call<LoginBean>, response: Response<LoginBean>) {
            }
        })
    }

    private fun get() {
        val retrofit = RetrofitBuilder()
                .baseUrl(KalleUrl.BASE_URL)
                .addParams(extraTestParamMap)
                .build(this)
        val kalleApi = retrofit.create(IKalleApi::class.java)
        val get = kalleApi.get("guest", "25")
        get.enqueue(object : Callback<GetBean> {
            override fun onFailure(call: Call<GetBean>, t: Throwable) {
            }

            override fun onResponse(call: Call<GetBean>, response: Response<GetBean>) {
            }
        })
    }

    private fun post() {
        val builder = FormBody.Builder()
        builder.addEncoded("name", "guest")
        builder.addEncoded("age", "25")
        val retrofit = RetrofitBuilder()
                .baseUrl(KalleUrl.BASE_URL)
                .build(this)
        val formBody = builder.build()
        val kalleApi = retrofit.create(IKalleApi::class.java)
        val post = kalleApi.post(formBody)

        post.enqueue(object : Callback<PostBean> {
            override fun onFailure(call: Call<PostBean>, t: Throwable) {
            }

            override fun onResponse(call: Call<PostBean>, response: Response<PostBean>) {
            }
        })
    }

    private fun pickImage() {
        Album.image(this)
                .multipleChoice()
                .selectCount(3)
                .camera(true)
                .columnCount(3)
                .onResult { _, result ->
                    val fileMap = TreeMap<String, File>()
                    for (i in 0 until result.size) {
                        fileMap["file${i + 1}"] = File(result[i].path)
                    }
                    upload(fileMap)
                }
                .start()
    }

    private fun upload(fileMap: TreeMap<String, File>) {
        val retrofit = RetrofitBuilder()
                .baseUrl(KalleUrl.BASE_URL)
                .build(this)
        val kalleApi = retrofit.create(IKalleApi::class.java)
        val name = RequestBodyBuilder.createText("guest")
        val age = RequestBodyBuilder.createText("25")
        val partList = RequestBodyBuilder.createMultipartBodyPartList(fileMap)
        val uploadFile = kalleApi.uploadFile(name, age, partList)
        uploadFile.enqueue(object : Callback<UploadBean> {
            override fun onFailure(call: Call<UploadBean>, t: Throwable) {
            }

            override fun onResponse(call: Call<UploadBean>, response: Response<UploadBean>) {
            }
        })
    }

    private fun download() {
        val retrofit = RetrofitBuilder()
                .baseUrl(KalleUrl.BASE_URL)
                .build(this)
        val kalleApi = retrofit.create(IKalleApi::class.java)
//        val downloadFile = kalleApi.downloadFile("upload/1527220017003e5258758-d4a4-495d-bd07-1eb3a6633f39.jpg")
        val downloadFile = kalleApi.downloadFile("http://cdn.aixifan.com/downloads/AcFun-portal-release-5.7.0.575-575.apk")
        Log.i("test", "start enqueue")
        downloadFile.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val cacheFile = CacheUtils.createFormatedCacheFile(this@MainActivity, "apk")
                if (cacheFile != null && response.body() != null) {
                    FileUtils.writeFileByIS(cacheFile, response.body()!!.byteStream(), false)
                }
            }
        })
    }

    private fun postJson() {
        val retrofit = RetrofitBuilder()
                .baseUrl(KalleUrl.BASE_URL)
                .build(this)
        val kalleApi = retrofit.create(IKalleApi::class.java)
        val simpleBean = SimpleBean("guest", "25")
        val postJson = kalleApi.postJson(simpleBean)
        postJson.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val result = response.body()?.string()
            }
        })
    }

}
