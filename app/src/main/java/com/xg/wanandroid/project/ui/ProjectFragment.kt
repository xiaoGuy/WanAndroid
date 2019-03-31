package com.xg.wanandroid.project.ui

import android.annotation.SuppressLint
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.xg.wanandroid.article.adapter.ArticlePagerAdapter
import com.xg.wanandroid.article.ui.ArticlesFragment
import com.xg.wanandroid.common.ScrollToTop
import com.xg.wanandroid.common.ui.BaseFragment
import com.xg.wanandroid.common.view.SimpleOnTabSelectedListener
import com.xg.wanandroid.core.extension.ioToMainThread
import com.xg.wanandroid.network.api.Api
import wanandroid.xg.com.wanandroid.R

class ProjectFragment : BaseFragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun getLayoutId() = R.layout.layout_tablayout_viewpager2
    override fun title() = "项目"
    override fun showToolBarShadow() = false

    override fun initViews(view: View) {
        viewPager = view.findViewById(R.id.viewPager2)
        tabLayout = view.findViewById(R.id.tabLayout2)
        tabLayout.addOnTabSelectedListener(object: SimpleOnTabSelectedListener() {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val adapter = viewPager.adapter
                    if (adapter is FragmentPagerAdapter) {
                        val fragment = adapter.getItem(tab.position)
                        if (fragment is ScrollToTop) {
                            fragment.scrollToTop()
                        }
                    }
                }
            }
        })
    }

    override fun onFirstShown() {
        showLoadingView()
        loadData()
    }

    @SuppressLint("CheckResult")
    private fun loadData() {
        Api.service.getProjectType()
                .ioToMainThread()
                .subscribe({
                    if (it.data.isEmpty()) {
                        showEmptyView()
                    } else {
                        val adapter = ArticlePagerAdapter(_mActivity.supportFragmentManager,
                                ArticlesFragment.TYPE_PROJECT, it.data)
                        viewPager.adapter = adapter
                        tabLayout.setupWithViewPager(viewPager)
                        showContentView()
                    }
                }, {
                    if (it.message != null) {
                        showErrorView(it.message!!)
                    }
                })
    }

}