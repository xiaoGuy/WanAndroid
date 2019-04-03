package com.xg.wanandroid.network.converter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.xg.wanandroid.network.exception.ApiException
import com.xg.wanandroid.network.modal.BaseResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import wanandroid.xg.com.core.extension.logError

class CustomGsonResponseBodyConverter<T>
    internal constructor(val gson: Gson, val adapter: TypeAdapter<T>): Converter<ResponseBody, T> {

    override fun convert(value: ResponseBody): T {
        val baseResponse = gson.fromJson(value.string(), BaseResponse::class.java)
        try {
            return if (baseResponse.errorCode != 0) {
                throw ApiException(baseResponse.errorCode, baseResponse.errorMsg)
            } else {
                // 纯操作类的接口比如"收藏"，"取消收藏"，它们的 data 永远都是 null，而 null 不能被解析成对象
                adapter.fromJson(gson.toJson(baseResponse.data ?: ""))
            }
        } catch (e: Exception) {
            baseResponse.data?.let {
                logError(gson.toJson(it), e)
            }
            throw e
        } finally {
            value.close()
        }
    }

}