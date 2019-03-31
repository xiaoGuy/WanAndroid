package com.xg.wanandroid.util

import android.app.Activity
import android.content.Context
import com.blankj.utilcode.util.Utils
import com.xg.wanandroid.account.ui.LoginActivity
import com.xg.wanandroid.network.cookies.CookieJarImp

object LoginUtils {

    var isLogin = false

    fun refreshLoginState() {
        isLogin = CookieJarImp.hasCookies()
    }

    fun logout() {
        isLogin = false
        CookieJarImp.clearCookies()
    }

    fun login(context: Context, callback: (login: Boolean) -> Unit) {
        if (isLogin) {
            callback(true)
        } else {
            Utils.getApp().registerActivityLifecycleCallbacks(object: SimpleActivityLifecycleCallbacks() {
                override fun onActivityDestroyed(activity: Activity?) {
                    if (activity is LoginActivity) {
                        Utils.getApp().unregisterActivityLifecycleCallbacks(this)
                        callback(isLogin)
                    }
                }
            })
            LoginActivity.start(context)
        }
    }
}