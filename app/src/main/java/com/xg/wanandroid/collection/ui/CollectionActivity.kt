package com.xg.wanandroid.collection.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.blankj.utilcode.util.ColorUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xg.wanandroid.article.adapter.ArticleListAdapter
import com.xg.wanandroid.common.ui.BaseActivity
import com.xg.wanandroid.common.view.SpaceItemDecoration
import com.xg.wanandroid.core.extension.ioToMainThread
import com.xg.wanandroid.core.extension.showToast
import com.xg.wanandroid.network.api.Api
import kotlinx.android.synthetic.main.layout_swipe_refresh_recycler_view.*
import wanandroid.xg.com.wanandroid.R

class CollectionActivity: BaseActivity(), SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CollectionActivity::class.java))
        }
    }

    private lateinit var adapter: ArticleListAdapter
    private var pageNo = 0

    override fun getLayoutId() = R.layout.layout_swipe_refresh_recycler_view

    override fun initViews(view: View, savedInstanceState: Bundle?) {
        updateTitle("收藏的文章")

        swipeRefreshLayout.apply {
            setColorSchemeColors(ColorUtils.getColor(R.color.colorPrimary))
            setOnRefreshListener(this@CollectionActivity)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CollectionActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(SpaceItemDecoration(this@CollectionActivity))
        }
        adapter = ArticleListAdapter().apply {
            bindToRecyclerView(recyclerView)
            setOnLoadMoreListener(this@CollectionActivity, recyclerView)
        }

        showLoadingView()
        onRefresh()
    }

    override fun onReload() {
        onRefresh()
    }

    @SuppressLint("CheckResult")
    override fun onRefresh() {
        adapter.setEnableLoadMore(false)

        Api.service.getCollectArticles(0)
                .ioToMainThread()
                .subscribe({
                    adapter.setEnableLoadMore(true)
                    swipeRefreshLayout.isRefreshing = false

                    if (it.data.isEmpty()) {
                        showEmptyView()
                    } else {
                        showContentView()
                        adapter.setNewData(it.data)
                        pageNo = 0
                        if (it.over) {
                            adapter.loadMoreEnd()
                        }
                    }
                }, {
                    adapter.setEnableLoadMore(true)
                    swipeRefreshLayout.isRefreshing = false

                    if (adapter.data.isEmpty()) {
                        showErrorView("请求失败")
                    } else {
                        showToast(it.message)
                    }
                })
    }

    @SuppressLint("CheckResult")
    override fun onLoadMoreRequested() {
        swipeRefreshLayout.isEnabled = false

        Api.service.getCollectArticles(pageNo)
                .ioToMainThread()
                .subscribe({
                    adapter.addData(it.data)
                    if (it.over) {
                        adapter.loadMoreEnd()
                    } else {
                        adapter.loadMoreComplete()
                        pageNo ++
                    }
                }, {
                    adapter.loadMoreFail()
                })
    }
}