package com.zeropercenthappy.retrofitutil.call_adapter.coroutine_call_adapter

import com.zeropercenthappy.utilslibrary.utils.ZPHLogger
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CoroutineCallAdapterFactory : CallAdapter.Factory(), ZPHLogger {

    override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)
        return if (rawType == CoroutineCall::class.java) {
            val responseType = getParameterUpperBound(0, returnType as ParameterizedType)
            return CoroutineCallAdapter<Any>(responseType)
        } else {
            null
        }
    }

}