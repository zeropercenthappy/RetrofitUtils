package com.zeropercenthappy.retrofitutil

import com.zeropercenthappy.utilslibrary.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object RequestBodyBuilder {
    @JvmStatic
    fun createText(value: String): RequestBody {
        return value.toRequestBody(ContentType.TEXT.value.toMediaTypeOrNull())
    }

    @JvmStatic
    fun createMultipartBodyPartList(fileMap: Map<String, File>): List<MultipartBody.Part> {
        val partList = arrayListOf<MultipartBody.Part>()
        for (key in fileMap.keys) {
            val file = fileMap[key]
            file?.apply {
                val mimeType = FileUtils.getFileMimeType(file)
                mimeType?.apply {
                    val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData(key, file.name, requestBody)
                    partList.add(part)
                }
            }
        }
        return partList
    }

    @JvmStatic
    fun createJson(value: String): RequestBody {
        return value.toRequestBody(ContentType.JSON.value.toMediaTypeOrNull())
    }
}