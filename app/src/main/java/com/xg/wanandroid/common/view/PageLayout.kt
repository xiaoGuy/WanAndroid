package com.xg.wanandroid.common.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.blankj.utilcode.util.ColorUtils
import wanandroid.xg.com.wanandroid.R

class PageLayout(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    companion object {
        const val PAGE_EMPTY = 465
        const val PAGE_ERROR = 45
        const val PAGE_LOADING = 516
        const val PAGE_CONTENT = 883
    }

    interface OnReloadListener {
        fun onReload()
    }
    var mOnReloadListener: OnReloadListener? = null

    private var mCurrentPage = -1
    private var mEmptyView:    View? = null
    private var mErrorView:    View? = null
    private var mLoadingView:  View
    private var mContentView:  View? = null
    private val mLayoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(context, attrs)

    init {
        mLayoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
        mLayoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        mLayoutParams.gravity = Gravity.CENTER

        mLoadingView = ProgressBar(context, attrs)
        val pb = mLoadingView as ProgressBar
        pb.indeterminateTintList = ColorStateList.valueOf(ColorUtils.getColor(R.color.colorPrimary))
        setLoadingView(mLoadingView)

        val a = context.obtainStyledAttributes(attrs, R.styleable.PageLayout)
        val resId = a.getResourceId(R.styleable.PageLayout_contentView, -1)
        if (resId != -1) {
            setContentView(resId)
        }
        a.recycle()
    }

    fun setEmptyView(emptyView: View) {
        if (mEmptyView != null) {
            throw IllegalArgumentException("emptyView already set")
        }
        mEmptyView = emptyView
        emptyView.layoutParams = mLayoutParams
        addView(emptyView)
        emptyView.visibility = View.GONE

        /** emptyView 有可能没有填充整个 PageLayout，如果 emptyView 有背景色则无法填充整个 PageLayout
         *  所以这里提取出 emptyView 的背景，如果背景是纯色则直接用该颜色来当做 PageLayout 的背景色，
         * 将 emptyView 的背景色设置为透明是为了避免 overDraw
         */
        val bg = emptyView.background
        if (bg is ColorDrawable) {
            setBackgroundColor(bg.color)
            emptyView.setBackgroundColor(Color.TRANSPARENT)
        }
        emptyView.isClickable = false
    }

    fun setEmptyView(@LayoutRes layoutResId: Int) {
        if (mEmptyView != null) {
            throw IllegalArgumentException("emptyView already set")
        }
        setEmptyView(LayoutInflater.from(context).inflate(layoutResId, this, false))
    }

    fun setErrorView(errorView: View, retryView: View?, descView: TextView?) {
        if (mErrorView != null) {
            throw IllegalArgumentException("errorView already set")
        }
        mErrorView = errorView
        mErrorView?.tag = descView
        errorView.layoutParams = mLayoutParams
        addView(errorView)
        errorView.visibility = View.GONE

        val bg = errorView.background
        if (bg is ColorDrawable) {
            setBackgroundColor(bg.color)
            errorView.setBackgroundColor(Color.TRANSPARENT)
        }

        retryView?.setOnClickListener { _ ->
            showLoading()
            mOnReloadListener?.onReload()
        }
    }

    fun setErrorView(@LayoutRes layoutResId: Int, @IdRes clickableViewIdResId: Int = -1,
                     @IdRes descViewIdResId: Int = -1) {
        if (mErrorView != null) {
            throw IllegalArgumentException("errorView already set")
        }
        val errorView = LayoutInflater.from(context).inflate(layoutResId, this, false)
        var clickableView: View? = null
        var descView: TextView? = null
        if (clickableViewIdResId != -1) {
            clickableView = errorView.findViewById(clickableViewIdResId)
        }
        if (descViewIdResId != -1) {
            val v = errorView.findViewById<View>(descViewIdResId) as? TextView
                    ?: throw IllegalArgumentException("descView must be TextView")
            descView = v
        }
        setErrorView(errorView, clickableView, descView)
    }

    fun setLoadingView(loadingView: View) {
        mLoadingView = loadingView
        loadingView.layoutParams = mLayoutParams
        addView(loadingView)
        loadingView.visibility = View.GONE
    }

    fun setLoadingView(@LayoutRes layoutResId: Int) {
        setLoadingView(LayoutInflater.from(context).inflate(layoutResId, this, false))
    }

    fun setContentView(view: View) {
        if (mContentView != null) {
            throw IllegalArgumentException("contentView already set")
        }
        mContentView = view
        view.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(view)
        view.visibility = View.GONE
    }

    fun setContentView(@LayoutRes layoutResId: Int) {
        if (mContentView != null) {
            throw IllegalArgumentException("contentView already set")
        }
        setContentView(LayoutInflater.from(context).inflate(layoutResId, this, false))
    }

    fun showEmptyView() {
        checkNotNull("emptyView", mEmptyView)

        if (mCurrentPage != PAGE_EMPTY) {
            when (mCurrentPage) {
                PAGE_ERROR -> mErrorView?.visibility = View.GONE
                PAGE_LOADING -> mLoadingView.visibility = View.GONE
                PAGE_CONTENT -> mContentView?.visibility = View.GONE
            }
            mEmptyView?.visibility = View.VISIBLE
            mCurrentPage = PAGE_EMPTY
        }

        setOnClickListener {
            showLoading()
            mOnReloadListener?.onReload()
        }
    }

    fun showErrorView(errMsg: CharSequence) {
        checkNotNull("errorView", mErrorView)

        if (mCurrentPage != PAGE_ERROR) {
            when (mCurrentPage) {
                PAGE_LOADING -> mLoadingView.visibility = View.GONE
                PAGE_EMPTY -> mEmptyView?.visibility = View.GONE
                PAGE_CONTENT -> mContentView?.visibility = View.GONE
            }
            mErrorView?.visibility = View.VISIBLE
            val descView = mErrorView?.tag as TextView
            descView.text = errMsg
            mCurrentPage = PAGE_ERROR
        }

        setOnClickListener(null)
    }

    fun showLoading() {
        if (mCurrentPage != PAGE_LOADING) {
            when (mCurrentPage) {
                PAGE_ERROR -> mErrorView?.visibility = View.GONE
                PAGE_EMPTY -> mEmptyView?.visibility = View.GONE
                PAGE_CONTENT -> mContentView?.visibility = View.GONE
            }
            mLoadingView.visibility = View.VISIBLE
            mCurrentPage = PAGE_LOADING
        }
        setOnClickListener(null)
    }

    fun showContent() {
        checkNotNull("contentView", mContentView)

        if (mCurrentPage != PAGE_CONTENT) {
            when (mCurrentPage) {
                PAGE_ERROR -> mErrorView?.visibility = View.GONE
                PAGE_LOADING -> mLoadingView.visibility = View.GONE
                PAGE_EMPTY -> mEmptyView?.visibility = View.GONE
            }
            mContentView?.visibility = View.VISIBLE
            mCurrentPage = PAGE_CONTENT
        }
        setOnClickListener(null)
    }

    fun isLoading(): Boolean {
        return mLoadingView.visibility == View.VISIBLE
    }

    private fun checkNotNull(msg: String, obj: Any?) {
        if (obj == null) {
            throw NullPointerException("$msg is null")
        }
    }

}