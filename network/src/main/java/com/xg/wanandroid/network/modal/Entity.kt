package com.xg.wanandroid.network.modal

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
        val errorCode: Int,
        val errorMsg: String,
        val data: T
)

data class BannerData(
        val desc: String,
        val imagePath: String,
        val title: String,
        val url: String
)

data class Tag(
        val name: String,
        val url: String
)

data class ListResponse<T>(
        val curPage: Int,

        @SerializedName("datas")
        val data: List<T>,
        val offset: Int,

        /** 是否已经获取完所有数据 */
        val over: Boolean,

        /** 数据总共有多少页 */
        val pageCount: Int,

        /** 当前页面的数据数量 */
        val size: Int,
        val total: Int
)

data class Article(
        val author: String,
        val chapterName: String,
        val superChapterName: String = "",
        var collect: Boolean,
        val courseId: Int,
        val desc: String,
        val envelopePic: String,
        val fresh: Boolean,
        val id: Int,
        val link: String,
        val niceDate: String,
        val projectLink: String,
        val publishTime: Long,
        val tags: List<Tag>,
        val title: String,
        val zan: Int,

        /** 标记是否是置顶的文章 */
        var top: Boolean
)

data class Index(
        val id: Int,
        val name: String
)

data class KnowledgeHierarchy(
        val children: List<Index>,
        val name: String
)

data class Website(
        val link: String,
        val title: String
)

data class Navigation(
        val articles: List<Website>,
        val name: String
)