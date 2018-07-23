package com.zeropercenthappy.retrofitutil

import com.zeropercenthappy.utilslibrary.FileUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object RequestBodyBuilder {
    fun createText(value: String): RequestBody {
        return RequestBody.create(MediaType.parse(ContentType.TEXT.value), value)
    }

    fun createMultipartBodyPartList(fileMap: Map<String, File>): List<MultipartBody.Part> {
        val partList = arrayListOf<MultipartBody.Part>()
        for (key in fileMap.keys) {
            val file = fileMap[key]
            file?.apply {
                val mimeType = FileUtils.getFileMimeType(file)
                mimeType?.apply {
                    val requestBody = RequestBody.create(MediaType.parse(mimeType), file)
                    val part = MultipartBody.Part.createFormData(key, file.name, requestBody)
                    partList.add(part)
                }
            }
        }
        return partList
    }
}