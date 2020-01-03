package com.zeropercenthappy.retrofitutilsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.yanzhenjie.album.Album
import com.zeropercenthappy.okhttploginterceptor.OkHttpLogInterceptor
import com.zeropercenthappy.retrofitutil.RequestBodyBuilder
import com.zeropercenthappy.retrofitutil.RetrofitBuilder
import com.zeropercenthappy.retrofitutil.call_adapter.coroutine_call_adapter.CoroutineCallAdapterFactory
import com.zeropercenthappy.retrofitutil.converter.string_converter.StringConverterFactory
import com.zeropercenthappy.retrofitutilsample.api.IKalleApi
import com.zeropercenthappy.retrofitutilsample.api.KalleUrl
import com.zeropercenthappy.retrofitutilsample.pojo.*
import com.zeropercenthappy.utilslibrary.utils.CacheUtils
import com.zeropercenthappy.utilslibrary.utils.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import me.jessyan.progressmanager.ProgressListener
import me.jessyan.progressmanager.ProgressManager
import me.jessyan.progressmanager.body.ProgressInfo
import okhttp3.FormBody
import okhttp3.ResponseBody
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var extraTestParamMap: Map<String, String>
    private lateinit var kalleApi: IKalleApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        extraTestParamMap = mapOf("aTopKey" to "aTopValue", "customKey" to "customValue")

        val retrofit = RetrofitBuilder()
            .addInterceptor(OkHttpLogInterceptor())
            .baseUrl(KalleUrl.BASE_URL)
            .addParams(extraTestParamMap)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(StringConverterFactory())
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
                    info { "login request success" }
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
                    info { "get request success" }
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
        // 为了避免添加的测试用的公共参数影响ProgressManager获取进度，这里重新构建Retrofit
        // 实际业务中根据具体情况，可以对url进行处理、拼接后，将实际的url传给ProgressManager
        val tempRetrofit = RetrofitBuilder()
            .addInterceptor(OkHttpLogInterceptor())
            .baseUrl(KalleUrl.BASE_URL)
            .build(this@MainActivity)
        val tempKalleApi = tempRetrofit.create(IKalleApi::class.java)
        //progress
        ProgressManager.getInstance()
            .addRequestListener(KalleUrl.BASE_URL + KalleUrl.UPLOAD, object : ProgressListener {
                override fun onProgress(progressInfo: ProgressInfo) {
                    info { "progress:${progressInfo.percent}%" }
                }

                override fun onError(id: Long, e: Exception?) {
                    e?.printStackTrace()
                    error(e?.localizedMessage)
                }
            })
        //
        val name = RequestBodyBuilder.createText("guest")
        val age = RequestBodyBuilder.createText("25")
        val fileList = RequestBodyBuilder.createMultipartBodyPartList(fileMap)

        val uploadFile = tempKalleApi.uploadFile(name, age, fileList)
        uploadFile.enqueue(object : Callback<UploadBean> {
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
                    info { "upload request success" }
                }
            }
        })

//        val paramMap = TreeMap<String, RequestBody>()
//        paramMap["name"] = RequestBodyBuilder.createText("guest")
//        paramMap["age"] = RequestBodyBuilder.createText("25")
//        val uploadFile1 = tempKalleApi.uploadFile1(paramMap, fileList)
//        uploadFile1.enqueue(object : Callback<UploadBean> {
//            override fun onFailure(call: Call<UploadBean>, t: Throwable) {
//                if (call.isCanceled) {
//                    // cancel
//                    info { "upload request cancel" }
//                } else {
//                    // fail
//                    info { "upload request fail" }
//                }
//            }
//
//            override fun onResponse(call: Call<UploadBean>, response: Response<UploadBean>) {
//                if (response.isSuccessful && response.body() != null) {
//                    // success
//                    info { "upload request success" }
//                }
//            }
//        })
    }

    private fun download() {
        // 为了避免添加的测试用的公共参数影响ProgressManager获取进度，这里重新构建Retrofit
        // 实际业务中根据具体情况，可以对url进行处理、拼接后，将实际的url传给ProgressManager
        val tempRetrofit = RetrofitBuilder()
            .addInterceptor(OkHttpLogInterceptor())
            .baseUrl(KalleUrl.BASE_URL)
            .build(this@MainActivity)
        val tempKalleApi = tempRetrofit.create(IKalleApi::class.java)
        val fileUrl = "https://imgs.aixifan.com/cms/2018_10_16/1539673075965.jpg"
        // progress
        ProgressManager.getInstance().addResponseListener(fileUrl, object : ProgressListener {
            override fun onProgress(progressInfo: ProgressInfo) {
                info { "progress:${progressInfo.percent}%" }
            }

            override fun onError(id: Long, e: Exception?) {
                e?.printStackTrace()
                error(e?.localizedMessage)
            }
        })
        //
        val downloadFile = tempKalleApi.downloadFile(fileUrl)
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
                        val cacheFile = CacheUtils.createFormatedCacheFile(this@MainActivity, "jpg")
                        if (cacheFile != null) {
                            val result = FileUtils.writeFileByIS(
                                cacheFile,
                                response.body()!!.byteStream(),
                                false
                            )
                            info { if (result) "download success" else "download failed" }
                        }
                    }
                }
            }
        })
    }

    private fun postJson() {
        val simpleBean = SimpleBean("guest", "25")
        val resultBody = RequestBodyBuilder.createJson(Gson().toJson(simpleBean))
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
