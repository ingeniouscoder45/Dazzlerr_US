package com.dazzlerr_usa.views.activities.blogs.pojos

 class AllCategoryBlogsPojo
 {

     val categories: MutableList<Category> ?= null
     val message: String = ""
     val result: MutableList<Result> ?= null
     val status: Boolean = false

     class Category(
        var cat_id: String = "",
        var name: String = ""
    )

     class Result(
        val _post_views: String = "",
        val _thumbnail_id: String = "",
        val blog_content: String = "",
        val blog_date: String = "",
        val blog_id: Int = 0,
        val blog_title: String = "",
        val image_url: String = "",
        var viewType: String = ""
    )
}