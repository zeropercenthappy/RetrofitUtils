package com.zeropercenthappy.retrofitutilsample.pojo

import com.google.gson.annotations.SerializedName

data class GetBean(
        @SerializedName("code") val code: Int = 0,
        @SerializedName("data") val data: Data = Data(),
        @SerializedName("succeed") val succeed: Boolean = false
) {
    data class Data(
            @SerializedName("name") val name: String = "",
            @SerializedName("age") val age: String = ""
    )
}