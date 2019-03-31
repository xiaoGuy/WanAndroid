package com.xg.wanandroid.hierarchy.model

import com.chad.library.adapter.base.entity.AbstractExpandableItem
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.xg.wanandroid.hierarchy.adapter.ExpandableItemAdapter

class ItemParent(val name: String) : AbstractExpandableItem<ItemChild>(), MultiItemEntity {

    override fun getLevel() = 0
    override fun getItemType() = ExpandableItemAdapter.TYPE_PARENT

}