package com.zeropercenthappy.retrofitutil.converter.string_converter

import okhttp3.ResponseBody
import retrofit2.Converter

class StringConverter : Converter<ResponseBody, String> {

    override fun convert(value: ResponseBody): String {
        return value.string()
    }
}