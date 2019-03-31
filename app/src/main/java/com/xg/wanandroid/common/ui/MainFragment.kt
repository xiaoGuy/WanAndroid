package com.xg.wanandroid.common.ui

import android.os.Bundle
import android.view.View
import com.xg.wanandroid.common.ScrollToTop
import com.xg.wanandroid.common.view.BottomBar
import com.xg.wanandroid.common.view.BottomBarTab
import com.xg.wanandroid.hierarchy.ui.HierarchyFragment
import com.xg.wanandroid.home.ui.HomeFragment
import com.xg.wanandroid.officialaccount.ui.OfficialAccountsFragment
import com.xg.wanandroid.project.ui.ProjectFragment
import wanandroid.xg.com.wanandroid.R

class MainFragment : BaseFragment() {

    companion object {
        const val FIRST = 0
        const val SECOND = 1
        const val THIRD = 2
        const val FOURTH = 3
    }

    private val mFragments = arrayOfNulls<BaseFragment>(4)
    private var mBottomBar: BottomBar? = null

    override fun getLayoutId() = R.layout.fragment_main
    override fun showToolBar() = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val firstFragment = findChildFragment(HomeFragment::class.java)
        if (firstFragment == null) {
            mFragments[FIRST] = HomeFragment()
            mFragments[SECOND] = ProjectFragment()
            mFragments[THIRD] = HierarchyFragment()
            mFragments[FOURTH] = OfficialAccountsFragment()

            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOURTH])
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment
            mFragments[SECOND] = findChildFragment(ProjectFragment::class.java)
            mFragments[THIRD] = findChildFragment(HierarchyFragment::class.java)
            mFragments[FOURTH] = findChildFragment(OfficialAccountsFragment::class.java)
        }
    }

    override fun initViews(view: View) {
        mBottomBar = view.findViewById<View>(R.id.bottomBar) as BottomBar

        mBottomBar?.addItem(BottomBarTab(_mActivity, R.drawable.vector_drawable_icon_home, "首页"))
                ?.addItem(BottomBarTab(_mActivity, R.drawable.vector_drawable_icon_project, "项目"))
                ?.addItem(BottomBarTab(_mActivity, R.drawable.vector_drawable_icon_hierarchy, "体系"))
                ?.addItem(BottomBarTab(_mActivity, R.drawable.vector_drawable_icon_official_accounts, "公众号"))

        mBottomBar?.setOnTabSelectedListener(object : BottomBar.OnTabSelectedListener {
            override fun onTabSelected(position: Int, prePosition: Int) {
                showHideFragment(mFragments[position], mFragments[prePosition])
            }

            override fun onTabUnselected(position: Int) {

            }

            override fun onTabReselected(position: Int) {
                val fragment = mFragments[position]
                if (fragment is ScrollToTop) {
                    fragment.scrollToTop()
                }
            }
        })
    }

}