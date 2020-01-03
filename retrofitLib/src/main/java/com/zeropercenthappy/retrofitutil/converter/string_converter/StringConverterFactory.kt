package com.zeropercenthappy.retrofitutil.converter.string_converter

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class StringConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type == String::class.java) {
            StringConverter()
        } else {
            super.responseBodyConverter(type, annotations, retrofit)
        }
    }
}