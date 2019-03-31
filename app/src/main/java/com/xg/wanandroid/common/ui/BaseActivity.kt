package com.xg.wanandroid.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.xg.wanandroid.common.view.PageLayout
import kotlinx.android.synthetic.main.activity_base.*
import me.yokeyword.fragmentation.SupportActivity
import wanandroid.xg.com.core.extension.logDebug
import wanandroid.xg.com.wanandroid.R

abstract class BaseActivity : SupportActivity() {

    private lateinit var emptyView: TextView
    private lateinit var errorView: Button
    private lateinit var myContentView: View

    abstract fun getLayoutId(): Int
    protected open fun showToolBar() = true
    protected open fun showBackButton() = true
    protected open fun onReload() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logDebug("onCreate $this")
        setContentView(R.layout.activity_base)
        setupToolbar()
        setupViews()
        initViews(myContentView, savedInstanceState)
    }

    protected open fun initViews(view: View, savedInstanceState: Bundle?) {
    }

    private fun setupToolbar() {
        if (!showToolBar()) {
            toolbar.visibility = View.GONE
        } else {
            toolbar.apply {
                setSupportActionBar(this)
                supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton())
                setNavigationOnClickListener {
                    finish()
                }
            }
        }
    }

    private fun setupViews() {
        myContentView = LayoutInflater.from(this).inflate(getLayoutId(), root, false)
        emptyView = TextView(this).apply {
            text = "这是空页面"
            textSize = 20.toFloat()
        }
        errorView = Button(this).apply {
            text = "这是错误页"
            textSize = 20.toFloat()
        }
        pageLayout.setContentView(myContentView)
        pageLayout.setEmptyView(emptyView)
        pageLayout.setErrorView(errorView, errorView, errorView)
        pageLayout.showContent()
        pageLayout.mOnReloadListener = object : PageLayout.OnReloadListener {
            override fun onReload() {
                this@BaseActivity.onReload()
            }
        }
    }

    fun showContentView() {
        pageLayout.showContent()
    }

    fun showEmptyView() {
        pageLayout.showEmptyView()
    }

    fun showErrorView(errMsg: String) {
        pageLayout.showErrorView(errMsg)
    }

    fun showLoadingView() {
        pageLayout.showLoading()
    }

    fun updateTitle(title: CharSequence) {
        if (showToolBar()) {
            supportActionBar?.title = title
        }
    }

}