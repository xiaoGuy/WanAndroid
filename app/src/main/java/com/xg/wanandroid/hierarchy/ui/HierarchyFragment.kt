package com.xg.wanandroid.hierarchy.ui

import android.annotation.SuppressLint
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.blankj.utilcode.util.ColorUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.xg.wanandroid.common.ScrollToTop
import com.xg.wanandroid.common.ui.BaseFragment
import com.xg.wanandroid.common.view.SpaceItemDecoration
import com.xg.wanandroid.core.extension.ioToMainThread
import com.xg.wanandroid.core.extension.showToast
import com.xg.wanandroid.hierarchy.adapter.ExpandableItemAdapter
import com.xg.wanandroid.hierarchy.model.ItemChild
import com.xg.wanandroid.hierarchy.model.ItemParent
import com.xg.wanandroid.network.api.Api
import wanandroid.xg.com.wanandroid.R

class HierarchyFragment: BaseFragment(), ScrollToTop, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpandableItemAdapter
    private var data = mutableListOf<MultiItemEntity>()

    override fun getLayoutId() = R.layout.fragment_hierarchy
    override fun title() = "体系"

    override fun initViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)

        adapter = ExpandableItemAdapter(data) {
            HierarchyArticlesActivity.start(_mActivity, it)
        }
        swipeRefreshLayout.apply {
            swipeRefreshLayout.setColorSchemeColors(ColorUtils.getColor(R.color.colorPrimary))
            swipeRefreshLayout.setOnRefreshListener(this@HierarchyFragment)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(_mActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(SpaceItemDecoration(_mActivity))
            adapter = this@HierarchyFragment.adapter
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
        Api.service.getKnowledgeHierarchy()
                .ioToMainThread()
                .subscribe({
                    swipeRefreshLayout.isRefreshing = false

                    if (it.isEmpty()) {
                        showEmptyView()
                    } else {
                        data.clear()
                        it.forEach { it ->
                            val itemParent = ItemParent(it.name)
                            it.children.forEach { it ->
                                itemParent.addSubItem(ItemChild(it))
                            }
                            data.add(itemParent)
                        }
                        adapter.notifyDataSetChanged()
                        showContentView()
                    }
                }, {
                    swipeRefreshLayout.isRefreshing = false

                    if (data.isEmpty()) {
                        showErrorView("请求失败")
                    } else {
                        showToast("请求失败")
                    }
                })
    }

    override fun onReload() {
        onRefresh()
    }

}