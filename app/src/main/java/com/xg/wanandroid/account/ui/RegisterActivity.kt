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
import com.xg.wanandroid.network.exception.ApiException
import kotlinx.android.synthetic.main.activity_register.*
import wanandroid.xg.com.wanandroid.R

class RegisterActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, RegisterActivity::class.java))
        }
    }

    override fun getLayoutId() = R.layout.activity_register
    override fun showToolBar() = false

    override fun initViews(view: View, savedInstanceState: Bundle?) {
        val textWatcher = SimpleTextWatcher(username, pwd, confirmPwd) {username, pwd, confirmPwd ->

            usernameLayout.error = if (username.isNotEmpty() && username.length <= 6) "用户名长度不能小于 6 位"
                                   else null
            pwdLayout.error     = if (pwd.isNotEmpty() && pwd.length < 6) "密码长度大不能小于 6 位"
                                  else null
            confirmPwdLayout.error = if (pwd.isNotEmpty() && confirmPwd.isNotEmpty() && pwd != confirmPwd) "两次密码不一致"
                                     else null
            btn.isEnabled = username.length >= 6 && pwd.length >= 6 && pwd == confirmPwd
        }
        username.addTextChangedListener(textWatcher)
        pwd.addTextChangedListener(textWatcher)
        confirmPwd.addTextChangedListener(textWatcher)

        btn.setOnClickListener {
            updateUI(true)
            Api.service.register(username.text.toString(), pwd.text.toString(), confirmPwd.text.toString())
                    .ioToMainThread()
                    .subscribe({
                        updateUI(false)
//                        if (it.errorCode != 0) {
//                            showToast(it.errorMsg)
//                        } else {
                            showToast("注册成功")
//                        }
                    }, {
                        updateUI(false)
                        val errorMsg = if (it is ApiException) it.errorMsg else {it.message ?: "登录失败"}
                        showToast(errorMsg)
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
            confirmPwdLayout.isEnabled = false
        } else {
            btn.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            usernameLayout.isEnabled = true
            pwdLayout.isEnabled = true
            confirmPwdLayout.isEnabled = true
        }
    }
}