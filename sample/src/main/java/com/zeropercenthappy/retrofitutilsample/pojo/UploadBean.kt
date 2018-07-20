package com.zeropercenthappy.retrofitutilsample.pojo

import com.google.gson.annotations.SerializedName

data class UploadBean(
        @SerializedName("code") val code: Int = 0,
        @SerializedName("succeed") val succeed: Boolean = false,
        @SerializedName("data") val data: Data = Data()
) {
    data class Data(
            @SerializedName("name") val name: String = "",
            @SerializedName("password") val password: String = "",
            @SerializedName("file1") val file1: String = "",
            @SerializedName("file2") val file2: String = "",
            @SerializedName("file3") val file3: String = ""
    )
}