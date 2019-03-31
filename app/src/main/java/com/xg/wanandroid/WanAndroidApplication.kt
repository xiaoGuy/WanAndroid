package com.xg.wanandroid

import android.app.Application
import android.support.multidex.MultiDex
import com.blankj.utilcode.util.Utils
import com.xg.wanandroid.util.LoginUtils

class WanAndroidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        LoginUtils.refreshLoginState()
        MultiDex.install(this)
    }

}