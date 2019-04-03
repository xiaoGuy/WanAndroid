package com.xg.wanandroid.network.api

import com.blankj.utilcode.util.Utils
import com.google.gson.GsonBuilder
import com.xg.wanandroid.core.extension.WanAndroid
import com.xg.wanandroid.network.converter.CustomGsonConverterFactory
import com.xg.wanandroid.network.cookies.CookieJarImp
import com.xg.wanandroid.network.interceptor.AddUserAgentInterceptor
import leavesc.hello.monitor.MonitorInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

object Api {

    private const val BASE_URL = "https://www.wanandroid.com/"
    val service: ApiService
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (WanAndroid.isDebug()) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(AddUserAgentInterceptor())
            .addNetworkInterceptor(loggingInterceptor)
            .addNetworkInterceptor(MonitorInterceptor(Utils.getApp()))
            .cookieJar(CookieJarImp)
            .build()

    private val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(CustomGsonConverterFactory.create(GsonBuilder().setPrettyPrinting().create()))
                .build()
        service = retrofit.create(ApiService::class.java)
    }

}