package com.dazzlerr_usa.views.activities.blogs.pojos

class BlogDetailsPojo
{
    val category: MutableList<Category>? = null
    val message: String? = null
    val result: MutableList<ResultBean>? = null
    val status: Boolean? = null
    val tags: MutableList<Tag>? = null

    class Category(
            val cat_id: Int? = null,
            val name: String? = null
    )

    class ResultBean(
            val _post_views: String? = null,
            val _thumbnail_id: String? = null,
            val blog_content: String? = null,
            val blog_date: String? = null,
            val blog_id: Int? = null,
            val blog_title: String? = null,
            val image_url: String? = null,
            val share_url: String? = null
    )

    class Tag(
            val name: String? = null,
            val tag_id: Int? = null
    )
}
