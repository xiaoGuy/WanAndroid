package com.xg.wanandroid.home.model

import com.xg.wanandroid.network.modal.Article
import com.xg.wanandroid.network.modal.BannerData

data class HomePageData(val bannerData: MutableList<BannerData>, val articles: MutableList<Article>)