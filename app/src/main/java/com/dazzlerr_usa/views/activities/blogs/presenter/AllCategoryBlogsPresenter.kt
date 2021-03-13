package com.dazzlerr_usa.views.activities.blogs.presenter

interface AllCategoryBlogsPresenter
{
    fun getAllCategoryBlogs(cat_id:String , tag_id :String , uname:String , page : String)
    fun getAllCategories(page : String)
    fun cancelRetrofitRequest()
}