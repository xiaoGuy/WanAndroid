package com.xg.wanandroid.core.extension

import android.annotation.SuppressLint
import android.app.Activity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
fun Activity.postDelay(mills: Long, runnable: () -> Unit) {
    Observable.just(1)
            .delay(mills, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                runnable()
            }
}