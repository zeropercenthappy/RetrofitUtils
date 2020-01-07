package com.zeropercenthappy.retrofitutil.service.download

import android.app.ActivityManager
import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.zeropercenthappy.retrofitutil.RetrofitBuilder
import com.zeropercenthappy.utilslibrary.utils.FileUtils
import com.zeropercenthappy.utilslibrary.utils.ZPHLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.Serializable

private const val EXTRA_DOWNLOAD_MAP = "DownloadService.download.map"

/**
 * Start a download service running in a independent process.
 *
 * Warning: The result of download is not reliable. Because download task may be terminated in progress.
 *
 * So you have to check the file integrity before use it.
 */
class DownloadService : IntentService("DownloadService"), ZPHLogger {

    private lateinit var api: DownloadApi
    private lateinit var downloadMap: Map<String, File>
    private lateinit var downloadTask: CoroutineScope

    companion object {
        @JvmStatic
        fun startDownloadTask(context: Context, downloadMap: Map<String, File>) {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(EXTRA_DOWNLOAD_MAP, downloadMap as Serializable)
            context.startService(intent)
        }

        @JvmStatic
        fun stopDownloadTask(context: Context) {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processes = am.runningAppProcesses
            for (process in processes) {
                if (process.processName == "${context.packageName}:ZphDownloadService") {
                    android.os.Process.killProcess(process.pid)
                    break
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        api = RetrofitBuilder()
            .baseUrl("http://localhost/")
            .build(this)
            .create(DownloadApi::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onHandleIntent(intent: Intent) {
        downloadMap = intent.getSerializableExtra(EXTRA_DOWNLOAD_MAP) as Map<String, File>
        download()
    }

    private fun download() = runBlocking {
        downloadTask = this
        for (url in downloadMap.keys) {
            val destinationFile = downloadMap[url] ?: continue
            val responseBody = api.download(url)
            FileUtils.writeFileByIS(destinationFile, responseBody.byteStream(), false)
        }
    }
}