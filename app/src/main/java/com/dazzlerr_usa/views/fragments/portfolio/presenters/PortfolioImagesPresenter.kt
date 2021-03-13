package com.dazzlerr_usa.views.fragments.portfolio.presenters

import java.io.File
import java.util.ArrayList

interface PortfolioImagesPresenter
{

    fun getPortfolioImages(user_id: String ,my_user_id:String, page:String,explore_portfolio :String)
    fun uploadImage(user_id: String , imageFile: File)
    fun deleteImage(user_id: String, selectedItemsToDelete: ArrayList<String>)
    fun cancelRetrofitRequest()
}