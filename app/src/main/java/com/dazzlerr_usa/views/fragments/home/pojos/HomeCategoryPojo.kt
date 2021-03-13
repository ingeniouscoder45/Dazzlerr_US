package com.dazzlerr_usa.views.fragments.home.pojos

class HomeCategoryPojo
{

    /**
     * status : User details Successfully!
     * success : true
     * data : [{"stat":4587,"user_role":"Model","role_img":"assets/images/default/model.jpg"},{"stat":501,"user_role":"Photographer","role_img":"assets/images/default/photographer.jpg"},{"stat":450,"user_role":"Anchor","role_img":"assets/images/default/anchor-icon.jpg"},{"stat":217,"user_role":"Actor","role_img":"assets/images/default/artists-icon.jpg"},{"stat":133,"user_role":"Makeup Artist","role_img":"assets/images/default/mua.jpg"}]
     */

    var status: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null
    var banners: MutableList<BannersBean>? = null

    class DataBean
    {
        /**
         * stat : 4587
         * user_role : Model
         * role_img : assets/images/default/model.jpg
         */

        var stat: String? = null
        var user_role: String? = null
        var role_img: String? = null
        var user_role_id: String? = null
    }

    class BannersBean {
        /**
         * stat : 4587
         * user_role : Model
         * role_img : assets/images/default/model.jpg
         */

        var banner_image: String? = null

    }

}