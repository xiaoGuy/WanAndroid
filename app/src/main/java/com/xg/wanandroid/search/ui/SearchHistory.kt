package com.example.zhangqinghua.myapplication.search

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xg.wanandroid.common.ui.BaseFragment
import com.xg.wanandroid.search.OnTagClickListener
import wanandroid.xg.com.wanandroid.R
import java.util.*

class SearchHistory : BaseFragment() {

    private val gson = Gson()
    private val searchHistory = LinkedList<String>()
    private var refresh = false
    private lateinit var root: View
    private lateinit var flexboxLayout: FlexboxLayout

    companion object {
        const val KEY = "search_history"
    }

    override fun getLayoutId() = R.layout.fragment_search_history
    override fun showToolBar() = false

    override fun initViews(view: View) {
        root = view
        flexboxLayout = view.findViewById(R.id.flowLayout)
        root.findViewById<View>(R.id.clear).setOnClickListener {
            AlertDialog.Builder(_mActivity)
                    .setMessage("确定要删除全部历史记录？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确认") {_, _ ->
                        clear()
                    }
                    .show()
        }

        loadRecord()
    }

    fun addRecord(keyword: String) {
        refresh = true
        if (searchHistory.isEmpty()) {
            root.visibility = View.VISIBLE
        }
        val index = searchHistory.indexOf(keyword)
        Log.d("fuck", "index $index")
        if (index == -1) {
            searchHistory.addFirst(keyword)
        } else if (index != 1) {
            searchHistory.removeAt(index)
            searchHistory.addFirst(keyword)
            flexboxLayout.removeViewAt(index)
        }
        if (index != 1) {
            flexboxLayout.addView(createTag(keyword), 0)
        }
    }

    fun clear() {
        root.visibility = View.GONE
        refresh = true
        searchHistory.clear()
        flexboxLayout.removeAllViews()
    }

    private fun loadRecord() {
        val json = SPUtils.getInstance(KEY).getString(KEY, "")
        if (json.isNotEmpty()) {
            val list: List<String> = Gson().fromJson(json, object : TypeToken<List<String>>(){}.type)
            searchHistory.apply {
                clear()
                addAll(list)
                forEach {it ->
                    flexboxLayout.addView(createTag(it))
                }
            }
        } else {
            root.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SPUtils.getInstance(KEY).put(KEY, gson.toJson(searchHistory))
    }

    private fun createTag(text: String): TextView {
        return TextView(_mActivity).apply {
            this.text = text
            setPadding(SizeUtils.dp2px(10f), SizeUtils.dp2px(5f), SizeUtils.dp2px(10f), SizeUtils.dp2px(5f))
            setTextColor(Color.parseColor("#656667"))
            val drawable = GradientDrawable().apply {
                color = ColorStateList.valueOf(Color.parseColor("#F7F8F9"))
                cornerRadius = height / 2f
            }
            background = drawable
            setOnClickListener {
                val activity = _mActivity
                if (activity is OnTagClickListener) {
                    activity.onTagClick(text)
                }
            }
        }
    }
}