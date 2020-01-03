package com.zeropercenthappy.retrofitutil.call_adapter.coroutine_call_adapter

import com.zeropercenthappy.utilslibrary.utils.ZPHLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call

data class CoroutineCall<T>(private val call: Call<T>) : ZPHLogger {

    @Throws(Exception::class)
    suspend fun request(): T = withContext(Dispatchers.IO) {
        val response = call.execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return@withContext body
        }
        val errorContent = response.errorBody()?.string()
        throw Exception(errorContent ?: "request fail")
    }!!

}