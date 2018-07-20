package com.zeropercenthappy.retrofitutilsample.pojo

import com.google.gson.annotations.SerializedName

data class LoginBean(
        @SerializedName("code") val code: Int = 0,
        @SerializedName("succeed") val succeed: Boolean = false
)