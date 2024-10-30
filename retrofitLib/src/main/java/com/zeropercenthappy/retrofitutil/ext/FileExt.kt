package com.zeropercenthappy.retrofitutil.ext

import android.webkit.MimeTypeMap
import java.io.File
import java.io.InputStream

val File.mimeType: String
    get() = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: ""

fun File.writeFromInputStream(inputStream: InputStream): Boolean {
    return try {
        this.outputStream().buffered().use { output ->
            inputStream.copyTo(output)
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}