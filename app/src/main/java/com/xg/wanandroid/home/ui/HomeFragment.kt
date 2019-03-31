package com.xg.wanandroid.home.ui

import android.annotation.SuppressLint
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.bingoogolapple.bgabanner.BGABanner
import com.blankj.utilcode.util.ColorUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xg.wanandroid.collection.ui.CollectionActivity
import com.xg.wanandroid.common.ScrollToTop
import com.xg.wanandroid.common.ui.BaseFragment
import com.xg.wanandroid.common.ui.WebViewActivity
import com.xg.wanandroid.common.view.SpaceItemDecoration
import com.xg.wanandroid.core.extension.ioToMainThread
import com.xg.wanandroid.core.extension.showToast
import com.xg.wanandroid.home.adapter.HomePageAdapter
import com.xg.wanandroid.home.model.HomePageData
import com.xg.wanandroid.network.api.Api
import com.xg.wanandroid.network.modal.Article
import com.xg.wanandroid.network.modal.BannerData
import com.xg.wanandroid.network.modal.BaseResponse
import com.xg.wanandroid.network.modal.ListResponse
import com.xg.wanandroid.search.ui.SearchActivity
import com.xg.wanandroid.util.LoginUtils
import io.reactivex.Observable
import io.reactivex.functions.Function3
import wanandroid.xg.com.wanandroid.R

class HomeFragment: BaseFragment(), ScrollToTop, SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener, Toolbar.OnMenuItemClickListener {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomePageAdapter
    private lateinit var banner: BGABanner
    private var pageNo = 0

    override fun getLayoutId() = R.layout.fragment_home
    override fun showToolBar() = true
    override fun title() = "WanAndroid"

    @SuppressLint("CheckResult")
    override fun initViews(view: View) {
        val toolbar = getToolbar()
        toolbar.inflateMenu(if (LoginUtils.isLogin) R.menu.menu_login else R.menu.menu)
        toolbar.setOnMenuItemClickListener(this)
        banner = LayoutInflater.from(_mActivity).inflate(R.layout.banner, view as ViewGroup, false) as BGABanner
        banner.apply {
            setAdapter { _, itemView, model, _ ->
                model as BannerData
                Glide.with(_mActivity)
                        .load(model.imagePath)
                        .into(itemView as ImageView)
            }
            setDelegate { _, _, bannerData, _ ->
                bannerData as BannerData
                WebViewActivity.start(_mActivity, bannerData.title, bannerData.url)
            }
        }

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.apply {
            setColorSchemeColors(ColorUtils.getColor(R.color.colorPrimary))
            setOnRefreshListener(this@HomeFragment)
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(_mActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(SpaceItemDecoration(_mActivity))
        }
        adapter = HomePageAdapter().apply {
            bindToRecyclerView(recyclerView)
            addHeaderView(banner)
            setOnLoadMoreListener(this@HomeFragment, recyclerView)
        }
    }

    override fun onFirstShown() {
        showLoadingView()
        onRefresh()
    }

    override fun scrollToTop() {
        recyclerView.scrollToPosition(0)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item != null) {
            when(item.itemId) {
                R.id.search -> {
                    SearchActivity.start(_mActivity)
                    return true
                }
                R.id.login -> {
                    LoginUtils.login(_mActivity) {
                        if (it) {
                            getToolbar().apply {
                                menu.clear()
                                inflateMenu(R.menu.menu_login)
                            }
                        }
                    }
                    return true
                }
                R.id.logout -> {
                    AlertDialog.Builder(_mActivity)
                            .setMessage("确定退出登录？")
                            .setPositiveButton("确定") {_, _ ->
                                LoginUtils.logout()

                                getToolbar().apply {
                                    menu.clear()
                                    inflateMenu(R.menu.menu)
                                }
                            }
                            .setNegativeButton("取消", null)
                            .show()
                    return true
                }
                R.id.collection -> {
                    CollectionActivity.start(_mActivity)
                    return true
                }
                else -> return false
            }
        } else {
            return false
        }
    }

    @SuppressLint("CheckResult")
    override fun onRefresh() {
        adapter.setEnableLoadMore(false)

        val getBannerData = Api.service.getBannerData()
        val getTopArticles = Api.service.getTopArticles()
        val getArticles = Api.service.getArticles(0)

        Observable.combineLatest(getBannerData, getTopArticles, getArticles,
                Function3<BaseResponse<List<BannerData>>, BaseResponse<List<Article>>, BaseResponse<ListResponse<Article>>, HomePageData> {
                    bannerData, topArticles, articles ->
                        val homePageData = HomePageData(mutableListOf(), mutableListOf())
                        homePageData.bannerData.addAll(bannerData.data)
                        homePageData.articles.addAll(topArticles.data)
                        homePageData.articles.forEach {
                            it.top = true
                        }
                        homePageData.articles.addAll(articles.data.data)
                        return@Function3 homePageData
                }).ioToMainThread()
                .subscribe({
                    swipeRefreshLayout.isRefreshing = false

                    if (it.articles.isEmpty()) {
                        showEmptyView()
                    } else {
                        showContentView()
                        setBannerData(it.bannerData)
                        adapter.setNewData(it.articles)
                        adapter.setEnableLoadMore(true)
                        pageNo = 1
                    }
                }, {
                    swipeRefreshLayout.isRefreshing = false

                    if (adapter.data.isEmpty()) {
                        showErrorView("请求失败")
                    } else {
                        showToast(it.message)
                    }
                })
    }

    override fun onReload() {
        onRefresh()
    }

    @SuppressLint("CheckResult")
    override fun onLoadMoreRequested() {
        swipeRefreshLayout.isEnabled = false

        Api.service.getArticles(pageNo)
                .ioToMainThread()
                .subscribe({
                    swipeRefreshLayout.isEnabled = true

                    adapter.addData(it.data.data)
                    if (it.data.over) {
                        adapter.loadMoreEnd()
                    } else {
                        adapter.loadMoreComplete()
                        pageNo ++
                    }
                }, {
                    adapter.loadMoreFail()
                })
    }

    private fun setBannerData(data: List<BannerData>) {
        val descList = mutableListOf<String>()
        data.forEach {
            descList.add(it.title)
        }
        banner.setData(data, descList)
    }
}