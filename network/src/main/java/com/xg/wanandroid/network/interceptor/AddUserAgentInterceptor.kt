package com.xg.wanandroid.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AddUserAgentInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
                .addHeader("User-Agent", "XgWanAndroid")
        return chain.proceed(builder.build())
    }

}