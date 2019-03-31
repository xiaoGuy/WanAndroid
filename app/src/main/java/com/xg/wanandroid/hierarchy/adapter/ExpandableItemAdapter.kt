package com.xg.wanandroid.hierarchy.adapter

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.xg.wanandroid.hierarchy.model.ItemChild
import com.xg.wanandroid.hierarchy.model.ItemParent
import com.xg.wanandroid.network.modal.Index
import wanandroid.xg.com.wanandroid.R

class ExpandableItemAdapter(list: MutableList<MultiItemEntity>, val callback: (index: Index) -> Unit) :
        BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(list) {

    companion object {
        const val TYPE_PARENT = 0
        const val TYPE_CHILD = 1
    }

    init {
        addItemType(TYPE_PARENT, android.R.layout.simple_list_item_1)
        addItemType(TYPE_CHILD, android.R.layout.simple_list_item_1)
    }

    override fun convert(helper: BaseViewHolder?, item: MultiItemEntity?) {
        if (helper != null && item != null) {
            when (helper.itemViewType) {
                TYPE_PARENT -> {
                    val tv = helper.itemView as TextView
                    tv.textSize = 17f
                    tv.setTextColor(Color.parseColor("#191919"))
                    tv.setBackgroundColor(Color.WHITE)
                    val itemParent = item as ItemParent
                    tv.text = itemParent.name
                    val icon = ContextCompat.getDrawable(mContext, if (item.isExpanded) R.drawable.arrow_bottom else R.drawable.arrow_right)
                    tv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icon, null)
                    tv.setOnClickListener {
                        val pos = helper.adapterPosition
                        if (item.isExpanded) {
                            collapse(pos)
                        } else {
                            expand(pos)
                        }
                    }
                }
                TYPE_CHILD -> {
                    val tv = helper.itemView as TextView
                    val itemChild = item as ItemChild
                    tv.text = itemChild.index.name
                    tv.textSize = 15f
                    tv.setTextColor(Color.parseColor("#2f2f2f"))
                    tv.setBackgroundColor(Color.parseColor("#f2f2f2"))
                    tv.setOnClickListener {
                        callback(itemChild.index)
                    }
                }
            }
        }
    }

}