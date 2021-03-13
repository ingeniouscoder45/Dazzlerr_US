package com.dazzlerr_usa.views.activities.blogs.presenter

interface BlogsListPresenter
{
    fun getBlogslist(type: Int , cat_id:String ,page:String)//1 for featured, 2 for Popular and 3 for recent Posts
    fun cancelRetrofitRequest()
}