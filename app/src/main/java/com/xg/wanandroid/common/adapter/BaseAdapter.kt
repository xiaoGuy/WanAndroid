package com.xg.wanandroid.common.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xg.wanandroid.common.ui.WebViewActivity
import com.xg.wanandroid.core.extension.ioToMainThread
import com.xg.wanandroid.network.api.Api
import com.xg.wanandroid.network.modal.Article
import wanandroid.xg.com.wanandroid.R

abstract class BaseAdapter : BaseQuickAdapter<Article, BaseViewHolder>(R.layout.item_article_list) {

    abstract fun showImage(): Boolean
    abstract fun showDigest(): Boolean
    abstract fun displayInHomePage(): Boolean

    override fun convert(helper: BaseViewHolder?, item: Article?) {
        if (helper != null && item != null) {
            helper.apply {
                setText(R.id.author, item.author)
                setText(R.id.title, item.title)
                setText(R.id.date, item.niceDate)

                if (displayInHomePage()) {
                    getView<View>(R.id.fresh).visibility = if (item.fresh) View.VISIBLE else View.GONE
                    getView<View>(R.id.top).visibility = if (item.top) View.VISIBLE else View.GONE
                } else {
                    getView<View>(R.id.fresh).visibility = View.GONE
                    getView<View>(R.id.top).visibility = View.GONE
                }

                if (showDigest() && item.desc.isNotEmpty()) {
                    setVisible(R.id.digest, true)
                    setText(R.id.digest, item.desc)
                } else {
                    setGone(R.id.digest, false)
                }

                if (!showImage() || item.envelopePic.isEmpty()) {
                    setGone(R.id.image, false)
                } else {
                    setVisible(R.id.image, true)
                    Glide.with(mContext)
                            .load(item.envelopePic)
                            .centerCrop()
                            .into(helper.getView(R.id.image))
                }

                if (!displayInHomePage() || item.tags.isEmpty()) {
                    setGone(R.id.tags, false)
                } else {
                    setVisible(R.id.tags, true)
                    val sb = StringBuilder()
                    for (tag in item.tags) {
                        sb.append(tag.name)
                        sb.append(" ")
                    }
                    val str = sb.toString().trim()
                    setText(R.id.tags, str)
                }

                val zeroMarginStart = getView<View>(R.id.top).visibility == View.GONE &&
                        getView<View>(R.id.fresh).visibility == View.GONE &&
                        getView<View>(R.id.tags).visibility == View.GONE
                helper.setGone(R.id.margin, !zeroMarginStart)

                if (TextUtils.isEmpty(item.superChapterName) && TextUtils.isEmpty(item.chapterName)) {
                    setGone(R.id.chapter, false)
                } else {
                    setVisible(R.id.chapter, true)
                    val sb = StringBuilder()
                    if (!TextUtils.isEmpty(item.superChapterName)) {
                        sb.append(item.superChapterName)
                        sb.append(" / ")
                    }
                    if (!TextUtils.isEmpty(item.chapterName)) {
                        sb.append(item.chapterName)
                    }
                    setText(R.id.chapter, sb.toString())
                }

                getView<ImageView>(R.id.collect).imageTintList = ColorStateList.valueOf(
                        if (item.collect) ColorUtils.getColor(R.color.colorPrimary) else Color.parseColor("#808080")
                )

                addOnClickListener(R.id.collect)
                setOnItemChildClickListener { adapter, view, position ->
                    val data = adapter.data[position] as Article
                    val response = if (data.collect) Api.service.cancelCollectArticle(data.id)
                                   else  Api.service.collectArticle(data.id)
                    response.ioToMainThread()
                            .subscribe({
                                if (it.errorCode == 0) {
                                    data.collect = !data.collect
                                    // BaseRecyclerViewAdapterHelper 能够自动跳过 HeaderView，返回正确的 position
                                    // 但是 notifyItemChanged 是 RecyclerView.Adapter 的方法，而且是 final，
                                    // 对于 RecyclerView.Adapter 来说，HeaderView 也是数据集的一部分，所以在调用
                                    // 局部刷新的方法时要跳过 HeaderView
                                    notifyItemChanged(headerLayoutCount + position)
                                } else {
                                    ToastUtils.showShort(it.errorMsg)
                                }
                            }, {
                                it.printStackTrace()
                            })
                }
                itemView.setOnClickListener {
                    WebViewActivity.start(mContext, item.title, item.link)
                }

            }
        }
    }


}