package com.xg.wanandroid.article.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.blankj.utilcode.util.ColorUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xg.wanandroid.article.adapter.ArticleListAdapter
import com.xg.wanandroid.common.ScrollToTop
import com.xg.wanandroid.common.ui.BaseFragment
import com.xg.wanandroid.common.view.SpaceItemDecoration
import com.xg.wanandroid.core.extension.ioToMainThread
import com.xg.wanandroid.network.api.Api
import com.xg.wanandroid.network.modal.Article
import com.xg.wanandroid.network.modal.BaseResponse
import com.xg.wanandroid.network.modal.ListResponse
import io.reactivex.Observable
import wanandroid.xg.com.wanandroid.R

class ArticlesFragment : BaseFragment(), ScrollToTop, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    companion object {
        const val CID = "cid"
        private const val TYPE = "type"

        const val TYPE_OFFICIAL_ACCOUNTS = 0
        const val TYPE_PROJECT = 1
        const val TYPE_HIERARCHY = 2

        fun newInstance(cid: Int, type: Int): ArticlesFragment {
            val fragment = ArticlesFragment()
            fragment.arguments = Bundle().apply {
                putInt(CID, cid)
                putInt(TYPE, type)
            }
            return fragment
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: ArticleListAdapter
    private var cid = -1
    private var type = -1
    private var pageNo = 0

    override fun getLayoutId() = R.layout.layout_swipe_refresh_recycler_view
    override fun showToolBar() = false

    override fun initViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)

        swipeRefreshLayout.apply {
            setColorSchemeColors(ColorUtils.getColor(R.color.colorPrimary))
            setOnRefreshListener(this@ArticlesFragment)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(_mActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(SpaceItemDecoration(_mActivity))
        }
        adapter = ArticleListAdapter().apply {
            bindToRecyclerView(recyclerView)
            setOnLoadMoreListener(this@ArticlesFragment, recyclerView)
        }

        arguments?.let {
            cid = it.getInt(CID, 0)
            type = it.getInt(TYPE, 0)
        }
    }

    override fun scrollToTop() {
        recyclerView.scrollToPosition(0)
    }

    override fun onFirstShown() {
        showLoadingView()
        onRefresh()
    }

    @SuppressLint("CheckResult")
    override fun onRefresh() {
        adapter.setEnableLoadMore(false)

        var response: Observable<BaseResponse<ListResponse<Article>>>? = null
        when(type) {
            TYPE_HIERARCHY -> response = Api.service.getArticlesByKnowledgeHierarchy(0, cid)
            TYPE_OFFICIAL_ACCOUNTS -> response = Api.service.getArticlesByOfficialAccount(0, cid)
            TYPE_PROJECT -> response = Api.service.getArticlesByProjectType(0, cid)
        }
            response?.ioToMainThread()
                ?.subscribe({
                    adapter.setEnableLoadMore(true)
                    swipeRefreshLayout.isRefreshing = false

                    if (it.data.data.isEmpty()) {
                        showEmptyView()
                    } else {
                        adapter.setNewData(it.data.data)
                        showContentView()
                    }
                    pageNo = 1
                }, {
                    showErrorView("请求失败")
                })
    }

    @SuppressLint("CheckResult")
    override fun onLoadMoreRequested() {
        swipeRefreshLayout.isEnabled = false

        var response: Observable<BaseResponse<ListResponse<Article>>>? = null
        when(type) {
            TYPE_HIERARCHY -> response = Api.service.getArticlesByKnowledgeHierarchy(pageNo, cid)
            TYPE_OFFICIAL_ACCOUNTS -> response = Api.service.getArticlesByOfficialAccount(pageNo, cid)
            TYPE_PROJECT -> response = Api.service.getArticlesByProjectType(pageNo, cid)
        }

        response?.ioToMainThread()
                ?.subscribe({
                    swipeRefreshLayout.isEnabled = true

                    adapter.addData(it.data.data)
                    if (it.data.over) {
                        adapter.loadMoreEnd()
                    } else {
                        pageNo ++
                        adapter.loadMoreComplete()
                    }
                }, {
                    adapter.loadMoreFail()
                })
    }

    override fun onReload() {
        onRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 让被 ViewPager 回收的 fragment 再次显示时自动请求数据
        firstShown = true
    }
}