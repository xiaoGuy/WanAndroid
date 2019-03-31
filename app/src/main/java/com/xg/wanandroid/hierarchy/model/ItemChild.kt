package com.xg.wanandroid.hierarchy.model

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.xg.wanandroid.hierarchy.adapter.ExpandableItemAdapter
import com.xg.wanandroid.network.modal.Index

class ItemChild(val index: Index) : MultiItemEntity {

    override fun getItemType() = ExpandableItemAdapter.TYPE_CHILD

}