package com.dazzlerr_usa.views.fragments.portfolio.views

import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioImagesPojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.UploadImagePojo
import java.util.ArrayList

interface PortfolioImagesView {
    fun onSuccess(model: PortfolioImagesPojo)
    fun onImageDelete(selectedItemsToDelete: ArrayList<String>)
    fun onImageUpload(model: UploadImagePojo)
    fun onFailed(message: String)
    fun showProgress(visiblity: Boolean)
    fun showProgress(visiblity: Int)
}