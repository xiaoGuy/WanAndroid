package com.xg.wanandroid.core.extension

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils

fun AppCompatActivity.showToast(msg: CharSequence?) {
    if (msg != null) {
        ToastUtils.showShort(msg)
    }
}

fun Fragment.showToast(msg: CharSequence?) {
    if (msg != null) {
        ToastUtils.showShort(msg)
    }
}