package com.dazzlerr_usa.views.activities.blogs.pojos

class BlogListPojo {

    var status = false
    var message: String? = null
    var result: MutableList<ResultBean>? = null

    class ResultBean
    {

        var blog_id = 0
        var blog_title: String? = null
        var blog_date: String? = null
        var image_url: String? = null
        var _post_views: String? = null
    }
}