package com.xg.wanandroid.search.ui

import android.annotation.SuppressLint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xg.wanandroid.common.ui.BaseFragment
import com.xg.wanandroid.common.view.SpaceItemDecoration
import com.xg.wanandroid.core.extension.ioToMainThread
import com.xg.wanandroid.home.adapter.HomePageAdapter
import com.xg.wanandroid.network.api.Api
import wanandroid.xg.com.wanandroid.R

// TODO 上一次请求没完成的时候就进行了下一次请求
class SearchFragment : BaseFragment(), BaseQuickAdapter.RequestLoadMoreListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomePageAdapter
    private var pageNo = 0
    private var keyword = ""

    override fun getLayoutId() = R.layout.fragment_search
    override fun showToolBar() = false

    override fun initViews(view: View) {
        adapter = HomePageAdapter()

        recyclerView = view as RecyclerView
        recyclerView.apply {
            layoutManager = LinearLayoutManager(_mActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(SpaceItemDecoration(_mActivity))
        }
        adapter.apply {
            bindToRecyclerView(recyclerView)
            setOnLoadMoreListener(this@SearchFragment, recyclerView)
        }
    }

    @SuppressLint("CheckResult")
    fun search(keyword: String) {
        this.keyword = keyword
        showLoadingView()

        Api.service.search(0, keyword)
                .ioToMainThread()
                .subscribe({
                    if (it.data.isEmpty()) {
                        showEmptyView()
                    } else {
                        adapter.setNewData(it.data)
                        pageNo = 1
                        showContentView()
                    }
                }, {
                    showErrorView("请求失败")
                })
    }

    override fun onReload() {
        search(keyword)
    }

    @SuppressLint("CheckResult")
    override fun onLoadMoreRequested() {
        Api.service.search(pageNo, keyword)
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