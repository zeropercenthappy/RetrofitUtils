package com.zeropercenthappy.retrofitutilsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import com.zeropercenthappy.retrofitutil.RequestBodyBuilder
import com.zeropercenthappy.retrofitutil.RetrofitBuilder
import com.zeropercenthappy.retrofitutil.RetrofitConfig
import com.zeropercenthappy.retrofitutilsample.api.IKalleApi
import com.zeropercenthappy.retrofitutilsample.api.KalleUrl
import com.zeropercenthappy.retrofitutilsample.pojo.*
import com.zeropercenthappy.retrofitutilsample.tool.AlbumImageLoader
import com.zeropercenthappy.utilslibrary.utils.CacheUtils
import com.zeropercenthappy.utilslibrary.utils.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import me.jessyan.progressmanager.ProgressListener
import me.jessyan.progressmanager.ProgressManager
import me.jessyan.progressmanager.body.ProgressInfo
import okhttp3.FormBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitConfig.DEBUG_MODE = true
        RetrofitConfig.LOG_LEVEL = HttpLoggingInterceptor.Level.BODY

        val imageLoaderConfig = ImageLoaderConfiguration.Builder(this)
                .build()
        ImageLoader.getInstance().init(imageLoaderConfig)
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(AlbumImageLoader())
                .build())

        ProgressManager.getInstance().setRefreshTime(1000)

        extraTestParamMap = mapOf("aTopKey" to "aTopValue", "customKey" to "customValue")

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
        val retrofit = RetrofitBuilder()
                .baseUrl(KalleUrl.BASE_URL)
                .addParams(extraTestParamMap)
                .build(this)
        val kalleApi = retrofit.create(IKalleApi::class.java)
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
        val retrofit = RetrofitBuilder()
                .baseUrl(KalleUrl.BASE_URL)
                .build(this)
        val formBody = builder.build()
        val kalleApi = retrofit.create(IKalleApi::class.java)
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
    }

    private fun download() {
        val fileUrl = "http://cdn.aixifan.com/downloads/AcFun-portal-release-5.7.0.575-575.apk"
        val retrofit = RetrofitBuilder()
                .baseUrl(KalleUrl.BASE_URL)
                .build(this)
        val kalleApi = retrofit.create(IKalleApi::class.java)
        val downloadFile = kalleApi.downloadFile(fileUrl)
        //progress
        ProgressManager.getInstance().addResponseListener(fileUrl, object : ProgressListener {
            override fun onProgress(progressInfo: ProgressInfo) {
                info("progress:${progressInfo.percent}%")
            }

            override fun onError(id: Long, e: Exception?) {
                e?.printStackTrace()
                error(e?.localizedMessage)
            }
        })
        //
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
                        val cacheFile = CacheUtils.createFormatedCacheFile(this@MainActivity, "apk")
                        if (cacheFile != null) {
                            val result = FileUtils.writeFileByIS(cacheFile, response.body()!!.byteStream(), false)
                            info { if (result) "download success" else "download failed" }
                        }
                    }
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
