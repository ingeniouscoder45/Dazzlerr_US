package com.dazzlerr_usa.views.activities.blogs.views

import com.dazzlerr_usa.views.activities.blogs.pojos.BlogDetailsPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogListPojo

interface BlogDetailsView
{
    fun onSuccess(model: BlogDetailsPojo)
    fun onGetSimilarPostsSuccess(model: BlogListPojo)
    fun onFailed(message: String)
    fun showProgress(visibility:Boolean)
}