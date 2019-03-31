package com.xg.wanandroid.hierarchy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.xg.wanandroid.article.ui.ArticlesFragment
import com.xg.wanandroid.common.ui.BaseActivity
import com.xg.wanandroid.network.modal.Index
import wanandroid.xg.com.wanandroid.R

class HierarchyArticlesActivity : BaseActivity() {

    companion object {
        private const val TITLE = "title"

        fun start(context: Context, index: Index) {
            val intent = Intent(context, HierarchyArticlesActivity::class.java).apply {
                putExtra(ArticlesFragment.CID, index.id)
                putExtra(TITLE, index.name)
            }
            context.startActivity(intent)
        }
    }

    override fun getLayoutId() = R.layout.activity_main
    override fun showToolBar() = true

    override fun initViews(view: View, savedInstanceState: Bundle?) {
        var cid = -1
        var title: CharSequence = ""
        intent?.let {
            cid = it.getIntExtra(ArticlesFragment.CID, cid)
            title = it.getCharSequenceExtra(TITLE)
        }

        updateTitle(title)

        if (findFragment(ArticlesFragment::class.java) == null) {
            loadRootFragment(R.id.container, ArticlesFragment.newInstance(cid, ArticlesFragment.TYPE_HIERARCHY))
        }
    }

}