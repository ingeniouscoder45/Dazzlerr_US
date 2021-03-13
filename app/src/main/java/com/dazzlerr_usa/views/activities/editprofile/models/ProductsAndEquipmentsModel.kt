package com.dazzlerr_usa.views.activities.editprofile.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProductsAndEquipmentsPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.UpdateServicePojo
import com.dazzlerr_usa.views.activities.editprofile.presenters.ProductsAndEquipmentsPresenter
import com.dazzlerr_usa.views.activities.editprofile.views.ProductsAndEquipmentsView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ProductsAndEquipmentsModel(val mContext: Context, val mView: ProductsAndEquipmentsView) : ProductsAndEquipmentsPresenter
{
    var getProductsService :Call<ProductsAndEquipmentsPojo>?=null
    var updateProductsService :Call<UpdateServicePojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(getProductsService!=null)
            getProductsService?.cancel()
    }

    override fun getProdutsOrEquipments(user_id: String) {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        getProductsService = webServices.getProfileProductsOrEquipments("application/x-www-form-urlencoded"   ,Constants.auth_key  ,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(getProductsService ,Constants.RETRY_COUNT, object : Callback<ProductsAndEquipmentsPojo>
        {
            override fun onResponse(call: Call<ProductsAndEquipmentsPojo>, response: Response<ProductsAndEquipmentsPojo>)
            {
                try
                {
                    mView.showProgress(false  )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ProfileServices response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onSuccess(response.body() as ProductsAndEquipmentsPojo)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ProductsAndEquipmentsPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")
            }
        })
    }

    override fun updateproducts(user_id: String, products: String, other_products: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        updateProductsService = webServices.updateProductsOrEquipments("application/x-www-form-urlencoded"   ,Constants.auth_key  ,user_id , products, other_products
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(updateProductsService ,Constants.RETRY_COUNT, object : Callback<UpdateServicePojo>
        {
            override fun onResponse(call: Call<UpdateServicePojo>, response: Response<UpdateServicePojo>)
            {
                try
                {
                    mView.showProgress(false  )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UpdateProductsOrEquipments response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onUpdateProductOrEquipmentSuccess()

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<UpdateServicePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }


}