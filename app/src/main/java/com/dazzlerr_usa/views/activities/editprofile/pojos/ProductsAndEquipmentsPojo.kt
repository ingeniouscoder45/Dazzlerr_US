package com.dazzlerr_usa.views.activities.editprofile.pojos

class ProductsAndEquipmentsPojo {
    var status: String? = null
    var success: Boolean = false
    var role_products: MutableList<RoleProductsBean>? = null

    class RoleProductsBean {

        var role_product_id: String? = null
        var type: String? = null
        var brand_name: String? = null
        var model_name: String? = null
        var brand_image: String? = null
        var is_added: Boolean = false
    }
}
