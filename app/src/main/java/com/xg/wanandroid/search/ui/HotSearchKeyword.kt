package com.example.zhangqinghua.myapplication.search

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xg.wanandroid.common.ui.BaseFragment
import com.xg.wanandroid.core.extension.ioToMainThread
import com.xg.wanandroid.network.api.Api
import com.xg.wanandroid.network.modal.Index
import com.xg.wanandroid.search.OnTagClickListener
import wanandroid.xg.com.core.extension.logDebug
import wanandroid.xg.com.wanandroid.R

class HotSearchKeyword : BaseFragment() {

    private lateinit var root: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BaseQuickAdapter<Index, BaseViewHolder>

    override fun getLayoutId() = R.layout.fragment_hot_search_keyword

    override fun initViews(view: View) {
        root = view
        recyclerView = view.findViewById(R.id.recyclerView)

        adapter = Adapter(_mActivity as OnTagClickListener)
        recyclerView.layoutManager = GridLayoutManager(_mActivity, 3)
        recyclerView.adapter = adapter

        loadRecord()
    }

    @SuppressLint("CheckResult")
    private fun loadRecord() {
        Api.service.getSearchHotKey()
                .ioToMainThread()
                .subscribe({
                    logDebug(it.toString())
                    if (it.data.isNotEmpty()) {
                        adapter.addData(it.data)
                        root.visibility = View.VISIBLE
                    }
                }, {
                    it.printStackTrace()
                })
    }

    class Adapter(private val l: OnTagClickListener) : BaseQuickAdapter<Index, BaseViewHolder>(android.R.layout.simple_list_item_1) {
        override fun convert(helper: BaseViewHolder?, item: Index?) {
            if (helper != null && item != null) {
                val tv = helper.itemView as TextView
                tv.apply {
                    text = item.name
                    textSize = 14f
                    setTextColor(Color.parseColor("#656667"))
                    setOnClickListener {
                        l.onTagClick(item.name)
                    }
                }
            }
        }
    }
}