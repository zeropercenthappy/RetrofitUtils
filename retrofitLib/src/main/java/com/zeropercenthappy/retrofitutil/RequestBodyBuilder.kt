package com.zeropercenthappy.retrofitutil

import com.zeropercenthappy.utilslibrary.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object RequestBodyBuilder {
    @JvmStatic
    fun createText(value: String): RequestBody {
        return RequestBody.create(ContentType.TEXT.value.toMediaTypeOrNull(), value)
    }

    @JvmStatic
    fun createMultipartBodyPartList(fileMap: Map<String, File>): List<MultipartBody.Part> {
        val partList = arrayListOf<MultipartBody.Part>()
        for (key in fileMap.keys) {
            val file = fileMap[key]
            file?.apply {
                val mimeType = FileUtils.getFileMimeType(file)
                mimeType?.apply {
                    val requestBody = RequestBody.create(mimeType.toMediaTypeOrNull(), file)
                    val part = MultipartBody.Part.createFormData(key, file.name, requestBody)
                    partList.add(part)
                }
            }
        }
        return partList
    }

    @JvmStatic
    fun createJson(value: String): RequestBody {
        return RequestBody.create(ContentType.JSON.value.toMediaTypeOrNull(), value)
    }
}