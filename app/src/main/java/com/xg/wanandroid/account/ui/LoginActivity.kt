package com.xg.wanandroid.account.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.xg.wanandroid.account.view.SimpleTextWatcher
import com.xg.wanandroid.common.ui.BaseActivity
import com.xg.wanandroid.core.extension.ioToMainThread
import com.xg.wanandroid.core.extension.showToast
import com.xg.wanandroid.network.api.Api
import com.xg.wanandroid.util.LoginUtils
import kotlinx.android.synthetic.main.activity_login.*
import wanandroid.xg.com.wanandroid.R

class LoginActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    override fun getLayoutId() = R.layout.activity_login
    override fun showToolBar() = false

    override fun initViews(view: View, savedInstanceState: Bundle?) {
        register.setOnClickListener {
            RegisterActivity.start(this)
        }
        val textWatcher = SimpleTextWatcher(username, pwd) {username, pwd, _ ->

            usernameLayout.error = if (username.isNotEmpty() && username.length < 6) "用户名长度不能小于 6 位"
                                   else null
            pwdLayout.error      = if (pwd.isNotEmpty() && pwd.length < 6) "密码长度不能小于 6 位"
                                   else null
            btn.isEnabled = username.length >= 6 && pwd.length >= 6

        }
        username.addTextChangedListener(textWatcher)
        pwd.addTextChangedListener(textWatcher)
        btn.setOnClickListener {
            updateUI(true)
            Api.service.login(username.text.toString(), pwd.text.toString())
                    .ioToMainThread()
                    .subscribe({
                        updateUI(false)
                        if (it.errorCode != 0) {
                            showToast(it.errorMsg)
                        } else {
                            LoginUtils.refreshLoginState()
                            finish()
                        }
                    }, {
                        updateUI(false)
                        showToast(it.message)
                    })
        }
        dismiss.setOnClickListener {
            finish()
        }
    }

    private fun updateUI(loading: Boolean) {
        if (loading) {
            btn.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            usernameLayout.isEnabled = false
            pwdLayout.isEnabled = false
        } else {
            btn.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            usernameLayout.isEnabled = true
            pwdLayout.isEnabled = true
        }
    }

}