package com.xg.wanandroid.article.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.xg.wanandroid.article.ui.ArticlesFragment
import com.xg.wanandroid.network.modal.Index

class ArticlePagerAdapter(fm: FragmentManager,
                          private val type: Int,
                          private val indices: List<Index>) : FragmentPagerAdapter(fm) {

    private val fragments = mutableListOf<Fragment>()

    init {
        indices.forEach {
            fragments.add(ArticlesFragment.newInstance(it.id, type))
        }
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
//        return ArticlesFragment.newInstance(indices[position].id, type)
    }
    override fun getCount() = indices.size
    override fun getPageTitle(position: Int): CharSequence? = indices[position].name

}