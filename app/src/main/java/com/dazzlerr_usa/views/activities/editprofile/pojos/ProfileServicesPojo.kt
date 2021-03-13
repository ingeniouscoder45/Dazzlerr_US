package com.dazzlerr_usa.views.activities.editprofile.pojos

class ProfileServicesPojo
{

    var status: String? = null
    var success: Boolean = false
    var role_services: MutableList<RoleServicesBean>? = null
    var role_products: MutableList<RoleProductsBean>? = null

    class RoleServicesBean
    {

        var service_name: String? = null
        var cat_name: String? = null
        var is_added: Boolean =false
        var shouldChangeCategory: Boolean =false
    }

    class RoleProductsBean
    {

        var role_product_id: Int = 0
        var type: String? = null
        var brand_name: String? = null
        var model_name: String? = null
        var brand_image: String? = null
    }
}
