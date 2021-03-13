package com.dazzlerr_usa.views.activities.blogs.views

import com.dazzlerr_usa.views.activities.blogs.pojos.AllCategoryBlogsPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogCategoriesPojo

interface AllCategoryBlogsView
{
    fun onSuccess(model: AllCategoryBlogsPojo)
    fun onGetCategories(model: BlogCategoriesPojo)
    fun onFailed(message: String)
    fun showProgress(visibility:Boolean)
}