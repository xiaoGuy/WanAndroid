package com.xg.wanandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.xg.wanandroid.common.ui.BaseActivity
import com.xg.wanandroid.common.ui.MainFragment
import wanandroid.xg.com.wanandroid.R

class MainActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    override fun getLayoutId() = R.layout.activity_main
    override fun showBackButton() = false
    override fun showToolBar() = false

    override fun initViews(view: View, savedInstanceState: Bundle?) {
        if (findFragment(MainFragment::class.java) == null) {
            loadRootFragment(R.id.container, MainFragment())
        }
    }
}
