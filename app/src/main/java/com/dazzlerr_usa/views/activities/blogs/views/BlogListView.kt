package com.dazzlerr_usa.views.activities.blogs.views

import com.dazzlerr_usa.views.activities.blogs.pojos.BlogListPojo

interface BlogListView
{
    fun onSuccess(model: BlogListPojo ,type: Int)// Type = 1 for featured, 2 for Popular and 3 for recent Posts
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}