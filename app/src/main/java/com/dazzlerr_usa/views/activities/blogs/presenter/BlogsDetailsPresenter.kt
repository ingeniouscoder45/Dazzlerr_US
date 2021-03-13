package com.dazzlerr_usa.views.activities.blogs.presenter

interface BlogsDetailsPresenter
{
    fun getBlogsDetails(blog_id: String)
    fun getSimilarBlogs(cat_id: String)
    fun cancelRetrofitRequest()
}
