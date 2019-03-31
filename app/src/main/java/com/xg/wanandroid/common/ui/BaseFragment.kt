package com.xg.wanandroid.common.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.blankj.utilcode.util.SizeUtils
import com.xg.wanandroid.common.view.PageLayout
import kotlinx.android.synthetic.main.activity_base.*
import me.yokeyword.fragmentation.SupportFragment
import wanandroid.xg.com.core.extension.logDebug
import wanandroid.xg.com.wanandroid.R

abstract class BaseFragment : SupportFragment() {

    private lateinit var pageLayout: PageLayout
    private lateinit var toolbar: Toolbar
    private lateinit var emptyView: TextView
    private lateinit var errorView: Button
    protected var firstShown = true

    abstract fun getLayoutId(): Int

    protected open fun title() = ""
    protected open fun showToolBar() = true
    protected open fun showToolBarShadow() = true

    protected open fun initViews(view: View) {
    }

    protected open fun onReload() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        logDebug("${hashCode()} : onCreateView")
        val root = inflater.inflate(R.layout.fragment_base, container, false)
        pageLayout = root.findViewById(R.id.pageLayout)
        toolbar = root.findViewById(R.id.toolbar)

        val contentView = inflater.inflate(getLayoutId(), pageLayout, false)
        pageLayout.setContentView(contentView)
        pageLayout.showContent()

        setupToolbar()
        setupViews()
        initViews(contentView)

        return root
    }

    private fun setupToolbar() {
        if (!showToolBar()) {
            toolbar.visibility = View.GONE
        } else {
            updateTitle(title())
            toolbar.elevation = if (showToolBarShadow()) SizeUtils.dp2px(16f).toFloat()
                                else SizeUtils.dp2px(0f).toFloat()
        }
    }

    private fun setupViews() {
        val contentView = LayoutInflater.from(_mActivity).inflate(getLayoutId(), root, false)
        emptyView = TextView(_mActivity).apply {
            text = "这是空页面"
            textSize = 20.toFloat()
            background = ColorDrawable(Color.WHITE)
        }
        errorView = Button(_mActivity).apply {
            text = "这是错误页"
            textSize = 20.toFloat()
            background = ColorDrawable(Color.WHITE)
        }
        pageLayout.setEmptyView(emptyView)
        pageLayout.setErrorView(errorView, errorView, errorView)
        pageLayout.showContent()
        pageLayout.mOnReloadListener = object : PageLayout.OnReloadListener {
            override fun onReload() {
                this@BaseFragment.onReload()
            }
        }
    }

    fun getToolbar(): Toolbar {
        return toolbar
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
        if (title.isEmpty()) {
            toolbar.visibility = View.GONE
        } else {
            toolbar.visibility = View.VISIBLE
            toolbar.title = title
        }
    }

    @CallSuper
    override fun onSupportVisible() {
        if (firstShown) {
            firstShown = false
            onFirstShown()
        }
    }

    protected open fun onFirstShown() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logDebug("${hashCode()} : onDestroyView")
    }

}