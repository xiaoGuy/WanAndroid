package com.xg.wanandroid.account.view

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class SimpleTextWatcher(private val username: EditText,
                        private val pwd: EditText,
                        private val confirmPwd: EditText? = null,
                        private val callback: (usernameStr: String, pwdStr: String, confirmPwdStr: String) -> Unit) : TextWatcher {

    init {
        callback(username.text.toString(), pwd.text.toString(), confirmPwd?.text.toString())
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        callback(username.text.toString(), pwd.text.toString(), confirmPwd?.text.toString())
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }
}