package com.dazzlerr_usa.views.activities.editprofile.presenters

interface ProductsAndEquipmentsPresenter
{

    fun getProdutsOrEquipments(user_id: String )// Products or equipments will automatically visible to user according to their role i.e photographer or Makeup artist
    fun updateproducts(user_id: String ,products :String, other_products:String)
    fun cancelRetrofitRequest()

}