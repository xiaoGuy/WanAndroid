package com.xg.wanandroid.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import com.example.zhangqinghua.myapplication.search.HotSearchKeyword
import com.example.zhangqinghua.myapplication.search.SearchHistory
import com.xg.wanandroid.common.ui.BaseActivity
import com.xg.wanandroid.core.extension.postDelay
import com.xg.wanandroid.search.OnTagClickListener
import kotlinx.android.synthetic.main.activity_search.*
import wanandroid.xg.com.wanandroid.R

class SearchActivity : BaseActivity(), OnTagClickListener {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SearchActivity::class.java))
        }
    }

    private lateinit var searchHistory: SearchHistory
    private lateinit var searchFragment: SearchFragment

    override fun getLayoutId() = R.layout.activity_search
    override fun showToolBar() = false

    override fun initViews(view: View, savedInstanceState: Bundle?) {
        searchHistory = SearchHistory()
        searchFragment = SearchFragment()

        loadRootFragment(R.id.room_top, searchHistory)
        loadRootFragment(R.id.room_bottom, HotSearchKeyword())
        cancel.setOnClickListener {
            onBackPressedSupport()
        }

        initSearchView()
    }

    private fun initSearchView() {
        searchView.queryHint = "请输入关键字"
        searchView.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        searchView.imeOptions = searchView.imeOptions or EditorInfo.IME_ACTION_SEARCH or
                EditorInfo.IME_FLAG_NO_EXTRACT_UI or EditorInfo.IME_FLAG_NO_FULLSCREEN
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @SuppressLint("CheckResult")
            override fun onQueryTextSubmit(query: String): Boolean {
                searchHistory.addRecord(query)
                if (searchFragment.isRemoving || !searchFragment.isAdded) {
                    searchFragment = SearchFragment()
                    loadRootFragment(R.id.room, searchFragment)
                }
                postDelay(200) {
                    searchFragment.search(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (TextUtils.isEmpty(query)) {
                    if (searchFragment.isSupportVisible) {
                        searchFragment.pop()
                    }
                }
                return true
            }
        })
    }

    override fun onTagClick(keyword: String) {
        searchView.setQuery(keyword, true)
    }

    override fun onBackPressedSupport() {
        finish()
    }

}