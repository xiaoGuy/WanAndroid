package com.xg.wanandroid.network.cookies

import com.blankj.utilcode.util.SPUtils
import com.xg.wanandroid.network.util.UrlManagement
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import wanandroid.xg.com.core.extension.logDebug

/**
 * 登录成功后服务器会返回 5 个 Cookie，它们的 Key 分别是
 *   loginUserName_wanandroid_com
 *   token_pass_wanandroid_com
 *   JSESSIONID
 *   loginUserName
 *   token_pass
 * JSESSIONID 保存的是登录的状态，所以实际上传递 Cookie 的时候传递 JSESSIONID 就够了，但是 JSESSIONID
 * 会过期，过期之后就需要重新登录来获取新的 JSESSIONID。有一个更简便的方法就是传递 Cookie 的时候把剩下的
 * 4 个 Cookie 也传递过去，这样在 JSESSIONID 过期之后，服务器就会自动对登录状态进行刷新，然后会将新的 JSESSIONID
 * 返回回来。
 */
object CookieJarImp : CookieJar {

    private const val SP_NAME = "Cookies"
    private const val KEY_JSESSIONID = "JSESSIONID"
    private val spUtils = SPUtils.getInstance(SP_NAME)
    private var cookies = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        if (!UrlManagement.isSaveCookieFrom(url)) {
            return
        }
        this.cookies = cookies
        val oldSessionId = spUtils.getString(KEY_JSESSIONID)
        cookies.forEach {
            spUtils.put(it.name(), it.toString())
        }
        val newSessionId = spUtils.getString(KEY_JSESSIONID)
        logDebug("cookies update![$url] old[$oldSessionId] new[$newSessionId]")
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        if (hasCookies() && UrlManagement.isCookieRequired(url)) {
             return if (cookies.isNotEmpty()) {
                logDebug("send cookies![$url] from cache")

                cookies
            } else {
                logDebug("send cookies![$url] from disk")

                val result = mutableListOf<Cookie>()
                spUtils.all.forEach {
                    Cookie.parse(url, it.value as String)?.let {cookie ->
                        result.add(cookie)
                    }
                }
                result
            }
        }
        return mutableListOf()
    }

    fun hasCookies(): Boolean {
        return spUtils.all.isNotEmpty()
    }

    fun clearCookies() {
        if (hasCookies()) {
            spUtils.clear()
        }
    }
}