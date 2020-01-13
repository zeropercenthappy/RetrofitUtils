package com.zeropercenthappy.retrofitutil.service.download

import android.app.ActivityManager
import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.zeropercenthappy.retrofitutil.RetrofitBuilder
import com.zeropercenthappy.utilslibrary.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.Serializable

private const val EXTRA_DOWNLOAD_MAP = "DownloadService.download.map"

/**
 * Download service which is running in a independent process.
 *
 */
class DownloadService : IntentService("DownloadService") {

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
            val cacheFile = checkCacheFile(destinationFile)
            if (destinationFile.exists()) {
                continue
            }
            val responseBody = api.download(url)
            val result = FileUtils.writeFileByIS(cacheFile, responseBody.byteStream())

            // Download success, move cache file to destination file
            if (result) {
                cacheFile.renameTo(destinationFile)
            }
        }
    }

    /**
     * @return Download cache file.
     */
    private fun checkCacheFile(destinationFile: File): File {
        val dir = destinationFile.parent
        return File(dir, "${destinationFile.name}.cache")
    }
}