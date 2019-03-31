package com.xg.wanandroid.network.api

import com.xg.wanandroid.network.modal.*
import io.reactivex.Observable
import retrofit2.http.*

interface ApiService {

    /**
     * 获取 Banner 数据
     */
    @GET("banner/json")
    fun getBannerData(): Observable<BaseResponse<List<BannerData>>>

    /**
     * 获取首页置顶的文章
     */
    @GET("article/top/json")
    fun getTopArticles(): Observable<BaseResponse<List<Article>>>

    /**
     * 获取首页的文章
     * @param pageNo 从 0 开始
     */
    @GET("article/list/{pageNo}/json")
    fun getArticles(@Path("pageNo") pageNo: Int): Observable<BaseResponse<ListResponse<Article>>>

    /**
     * 获取项目类型
     */
    @GET("project/tree/json")
    fun getProjectType(): Observable<BaseResponse<List<Index>>>

    /**
     * 获取指定项目类型下面的文章列表
     * @param pageNo 从 1 开始
     */
    @GET("project/list/{pageNo}/json")
    fun getArticlesByProjectType(
            @Path("pageNo") pageNo: Int,
            @Query("cid") cid: Int
    ): Observable<BaseResponse<ListResponse<Article>>>

    /**
     * 获取知识体系
     */
    @GET("tree/json")
    fun getKnowledgeHierarchy(): Observable<BaseResponse<List<KnowledgeHierarchy>>>

    /**
     * 获取指定的知识体系下的文章列表
     * @param pageNo 从 0 开始
     */
    @GET("article/list/{pageNo}/json")
    fun getArticlesByKnowledgeHierarchy(
            @Path("pageNo") pageNo: Int,
            @Query("cid") cid: Int
    ): Observable<BaseResponse<ListResponse<Article>>>

    /**
     * 获取公众号列表
     */
    @GET("wxarticle/chapters/json")
    fun getOfficialAccounts(): Observable<BaseResponse<List<Index>>>

    /**
     * 获取指定公众号下面的文章列表
     * @param pageNo 从 0 开始
     */
    @GET("wxarticle/list/{cid}/{pageNo}/json")
    fun getArticlesByOfficialAccount(
            @Path("pageNo") pageNo: Int,
            @Path("cid") cid: Int
    ): Observable<BaseResponse<ListResponse<Article>>>

    /**
     * 获取导航网站
     */
    @GET("navi/json")
    fun getNavigation(): Observable<BaseResponse<List<Navigation>>>

    /**
     * 获取收藏的文章，需要传递 Cookie
     */
    @GET("lg/collect/list/{pageNo}/json")
    fun getCollectArticles(@Path("pageNo") pageNo: Int): Observable<BaseResponse<ListResponse<Article>>>

    /**
     * 收藏站内文章
     */
    @POST("lg/collect/{id}/json")
    fun collectArticle(@Path("id") id: Int): Observable<BaseResponse<Any>>

    /**
     * 取消收藏（在文章列表中取消）
     */
    @POST("lg/uncollect_originId/{id}/json")
    fun cancelCollectArticle(@Path("id") id: Int): Observable<BaseResponse<Any>>

    /**
     * 获取搜索热词
     */
    @GET("hotkey/json")
    fun getSearchHotKey(): Observable<BaseResponse<List<Index>>>

    /**
     * 搜索关键字
     */
    @POST("article/query/{pageNo}/json")
    @FormUrlEncoded
    fun search(
            @Path("pageNo") pageNo: Int,
            @Field("k") keyword: String
    ): Observable<BaseResponse<ListResponse<Article>>>

    /**
     * 登录
     */
    @POST("user/login")
    @FormUrlEncoded
    fun login(
            @Field("username") username: String,
            @Field("password") password: String
    ): Observable<BaseResponse<Any>>

    /**
     * 注册
     */
    @POST("user/register")
    @FormUrlEncoded
    fun register(
            @Field("username")   username: String,
            @Field("password")   password: String,
            @Field("repassword") repassword: String
    ): Observable<BaseResponse<Any>>

}