package com.xg.wanandroid.network.util

import android.text.TextUtils
import com.xg.wanandroid.network.cookies.CookieJarImp
import okhttp3.HttpUrl
import wanandroid.xg.com.core.extension.logDebug

object UrlManagement {

    /**
     * 需要传递 Cookies 的 URL
     */
    private val cookiesRequiredUrls = mutableListOf<String>()
    private val cookiesGenerateUrls = mutableListOf<String>()

    init {
        cookiesRequiredUrls.apply {
            add("lg/collect/list/0/json")
            add("article/top/json")
            add("article/list/0/json")
            add("project/list/0/json")
            add("wxarticle/list/0/0/json")
            add("lg/collect/0/json")
            add("lg/uncollect_originId/0/json")
        }

        cookiesGenerateUrls.add("user/login")
    }

    fun isSaveCookieFrom(url: HttpUrl): Boolean {
        val result =
        if (CookieJarImp.hasCookies()) {
            val urls = mutableListOf<String>()
            urls.addAll(cookiesRequiredUrls)
            urls.addAll(cookiesGenerateUrls)
            isContain(url, urls)
        } else {
            isContain(url, cookiesGenerateUrls)
        }

        logDebug("isSaveCookieFrom[$url] $result")
        return result
    }

    fun isCookieRequired(url: HttpUrl): Boolean {
        val result =  isContain(url, cookiesRequiredUrls)

        logDebug("isCookieRequired[$url] $result")
        return result
    }

    private fun isContain(url: HttpUrl, urls: List<String>): Boolean {
        val path = getPath(url)
        var result = false
        for (item in urls) {
            if (path == item) {
                result = true
                break
            }
        }

        return result
    }

    private fun getPath(url: HttpUrl): String {
        val sb = StringBuilder()
        url.pathSegments().forEach {
            if (TextUtils.isDigitsOnly(it)) {
                sb.append("0")
            } else {
                sb.append(it)
            }
            sb.append("/")
        }
        // 去掉末尾的斜杠
        sb.delete(sb.length - 1, sb.length)
        return sb.toString()
    }
}