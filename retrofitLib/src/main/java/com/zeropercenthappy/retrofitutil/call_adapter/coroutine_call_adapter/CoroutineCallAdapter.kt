package com.zeropercenthappy.retrofitutil.call_adapter.coroutine_call_adapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class CoroutineCallAdapter<T>(private val responseType: Type) : CallAdapter<T, CoroutineCall<T>> {

    override fun adapt(call: Call<T>): CoroutineCall<T> {
        return CoroutineCall(call)
    }

    override fun responseType(): Type {
        return responseType
    }

}