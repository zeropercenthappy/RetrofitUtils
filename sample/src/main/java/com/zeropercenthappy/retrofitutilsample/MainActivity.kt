package com.zeropercenthappy.retrofitutilsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.yanzhenjie.album.Album
import com.zeropercenthappy.okhttp_log_interceptor.OkHttpLogInterceptor
import com.zeropercenthappy.retrofitutil.ContentType
import com.zeropercenthappy.retrofitutil.RetrofitBuilder
import com.zeropercenthappy.retrofitutilsample.api.IKalleApi
import com.zeropercenthappy.retrofitutilsample.pojo.*
import com.zeropercenthappy.utilslibrary.utils.CacheUtils
import com.zeropercenthappy.utilslibrary.utils.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var extraParamMap: Map<String, String>
    private lateinit var extraHeaderMap: Map<String, String>
    private lateinit var kalleApi: IKalleApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        extraParamMap = mapOf("extraParamKey" to "extraParamValue")
        extraHeaderMap = mapOf("extraHeaderKey" to "extraHeaderValue")

        val retrofit = RetrofitBuilder()
                .addInterceptor(OkHttpLogInterceptor("RetrofitTest"))
                .baseUrl(IKalleApi.BASE_URL)
                .addHeaders(extraHeaderMap)
                .addParams(extraParamMap)
                .addConverterFactory(GsonConverterFactory.create())
                .build(this)
        kalleApi = retrofit.create(IKalleApi::class.java)

        btn_login.setOnClickListener { login() }
        btn_get.setOnClickListener { get() }
        btn_post.setOnClickListener { post() }
        btn_upload.setOnClickListener { pickImage() }
        btn_download.setOnClickListener { download() }
        btn_post_json.setOnClickListener { postJson() }
    }

    private fun login() {
        val login = kalleApi.login("guest", "123456")
        login.enqueue(object : Callback<LoginBean> {
            override fun onFailure(call: Call<LoginBean>, t: Throwable) {
                if (call.isCanceled) {
                    // cancel
                    info { "login request cancel" }
                } else {
                    // fail
                    info { "login request fail" }
                }
            }

            override fun onResponse(call: Call<LoginBean>, response: Response<LoginBean>) {
                if (response.isSuccessful && response.body() != null) {
                    // success
                    info { "login request success: ${response.body()}" }
                }
            }
        })
    }

    private fun get() {
        val get = kalleApi.get("guest", "25")
        get.enqueue(object : Callback<GetBean> {
            override fun onFailure(call: Call<GetBean>, t: Throwable) {
                if (call.isCanceled) {
                    // cancel
                    info { "get request cancel" }
                } else {
                    // fail
                    info { "get request fail" }
                }
            }

            override fun onResponse(call: Call<GetBean>, response: Response<GetBean>) {
                if (response.isSuccessful && response.body() != null) {
                    // success
                    info { "get request success: ${response.body()}" }
                }
            }
        })
    }

    private fun post() {
        val builder = FormBody.Builder()
        builder.addEncoded("name", "guest")
        builder.addEncoded("age", "25")
        val formBody = builder.build()
        val post = kalleApi.post(formBody)

        post.enqueue(object : Callback<PostBean> {
            override fun onFailure(call: Call<PostBean>, t: Throwable) {
                if (call.isCanceled) {
                    // cancel
                    info { "post request cancel" }
                } else {
                    // fail
                    info { "post request fail" }
                }
            }

            override fun onResponse(call: Call<PostBean>, response: Response<PostBean>) {
                if (response.isSuccessful && response.body() != null) {
                    // success
                    info { "post request success" }
                }
            }
        })
    }

    private fun pickImage() {
        Album.image(this)
                .multipleChoice()
                .selectCount(3)
                .camera(true)
                .columnCount(3)
                .onResult { result ->
                    val fileMap = TreeMap<String, File>()
                    for ((index, albumFile) in result.withIndex()) {
                        fileMap["file${index + 1}"] = File(albumFile.path)
                    }
                    upload(fileMap)
                }
                .start()
    }

    private fun upload(fileMap: TreeMap<String, File>) {
        // 普通表单型数据
        val textMimeType = ContentType.TEXT.mimeType
        val nameBody = "guest".toRequestBody(textMimeType)
        val ageBody = "25".toRequestBody(textMimeType)
        // 二进制数据
        val filePartList = arrayListOf<MultipartBody.Part>()
        val imageMimeType = ContentType.IMAGE.mimeType
        for (entry in fileMap) {
            val key = entry.key
            val file = entry.value
            val requestBody = file.asRequestBody(imageMimeType)
            val part = MultipartBody.Part.createFormData(key, file.name, requestBody)
            filePartList.add(part)
        }
        // 发起
        kalleApi.uploadFile(nameBody, ageBody, filePartList).enqueue(object : Callback<UploadBean> {
            override fun onFailure(call: Call<UploadBean>, t: Throwable) {
                if (call.isCanceled) {
                    // cancel
                    info { "upload request cancel" }
                } else {
                    // fail
                    info { "upload request fail" }
                }
            }

            override fun onResponse(call: Call<UploadBean>, response: Response<UploadBean>) {
                if (response.isSuccessful && response.body() != null) {
                    // success
                    info { "upload request success: ${response.body()}" }
                }
            }
        })
    }

    private fun download() {
        val fileUrl = "https://imgs.aixifan.com/cms/2018_10_16/1539673075965.jpg"
        // val fileUrl = "https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fc5ec335a35b45bd84040a19172885a6~tplv-k3u1fbpfcp-watermark.image?imageslim"
        val downloadFile = kalleApi.downloadFile(fileUrl)
        downloadFile.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if (call.isCanceled) {
                    // cancel
                    info { "download request cancel" }
                } else {
                    // fail
                    info { "download request fail" }
                }
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // download completely
                doAsync {
                    if (response.isSuccessful && response.body() != null) {
                        val cacheFile = CacheUtils.createFormatCacheFile(this@MainActivity, "jpg", true)
                        val body = response.body()
                        if (cacheFile != null && body != null) {
                            val result = FileUtils.writeFileByIS(cacheFile, body.byteStream())
                            info { if (result) "download success" else "download failed" }
                        }
                    }
                }
            }
        })
    }

    private fun postJson() {
        val simpleBean = SimpleBean("guest", "25")
        val jsonValue = Gson().toJson(simpleBean)
        val mediaType = ContentType.JSON.value.toMediaType()
        val resultBody = jsonValue.toRequestBody(mediaType)
        val postJson = kalleApi.postJson(resultBody)
        postJson.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if (call.isCanceled) {
                    // cancel
                    info { "postJson request cancel" }
                } else {
                    // fail
                    info { "postJson request fail" }
                }
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    // success
                    info { "postJson request success" }
                }
            }
        })
    }
}
