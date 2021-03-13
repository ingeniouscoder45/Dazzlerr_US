package com.dazzlerr_usa.views.activities.editprofile.views

import com.dazzlerr_usa.views.activities.editprofile.pojos.ProductsAndEquipmentsPojo

interface ProductsAndEquipmentsView
{
    fun onSuccess(model: ProductsAndEquipmentsPojo)
    fun onUpdateProductOrEquipmentSuccess()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}