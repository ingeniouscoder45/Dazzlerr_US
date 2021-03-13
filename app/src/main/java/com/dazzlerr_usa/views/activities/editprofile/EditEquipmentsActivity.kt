package com.dazzlerr_usa.views.activities.editprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEditEquipmentsBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.editprofile.adapters.EditEquipmentsAdapter
import com.dazzlerr_usa.views.activities.editprofile.models.ProductsAndEquipmentsModel
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProductsAndEquipmentsPojo
import com.dazzlerr_usa.views.activities.editprofile.presenters.ProductsAndEquipmentsPresenter
import com.dazzlerr_usa.views.activities.editprofile.views.ProductsAndEquipmentsView
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class EditEquipmentsActivity : AppCompatActivity(), View.OnClickListener , ProductsAndEquipmentsView {

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var mPresenter: ProductsAndEquipmentsPresenter
    var filter_products_list  : ArrayList<String> = ArrayList()
    var mCustomProductsList : MutableList<String> = ArrayList()
    lateinit var bindingObj : ActivityEditEquipmentsBinding


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_edit_equipments)
        initializations()
        clicklisteners()
        apiCalling()
    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Equipments"
        mPresenter = ProductsAndEquipmentsModel(this, this)
    }


    fun clicklisteners()
    {

        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.submitEquipmentsBtn.setOnClickListener(this)
    }

    private fun apiCalling()
    {

        bindingObj.mainLayout.visibility = View.GONE // Initially hiding the layout when fetching the data from server

        if(this@EditEquipmentsActivity?.isNetworkActive()!!)
        {
            mPresenter.getProdutsOrEquipments(sharedPreferences.getString(Constants.User_id).toString())
        }

        else
        {

            val dialog = CustomDialog(this@EditEquipmentsActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling()
            })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    override fun onSuccess(model: ProductsAndEquipmentsPojo)
    {

        if(model?.role_products?.size!=0)
        {
            bindingObj.mainLayout.visibility = View.VISIBLE // Showing the layout after fetching the data from server

            val gManager = GridLayoutManager(this, 2)
            val gManager1 = GridLayoutManager(this, 2)
            bindingObj.editCamcoderRecyclerView.setLayoutManager(gManager)
            bindingObj.editCamerasRecyclerView.setLayoutManager(gManager1)

            var mCamerasList: MutableList<ProductsAndEquipmentsPojo.RoleProductsBean> = ArrayList()
            var mCamcodersList: MutableList<ProductsAndEquipmentsPojo.RoleProductsBean> = ArrayList()
            for(i in model.role_products!!.indices)
            {

                if(model.role_products?.get(i)?.type.equals("camera" ,ignoreCase = true))
                {
                    mCamerasList?.add(model.role_products!!.get(i))
                }
                else if(model.role_products?.get(i)?.type.equals("camecoder" ,ignoreCase = true))
                {
                    mCamcodersList?.add(model.role_products!!.get(i))
                }
            }

            bindingObj.editCamcoderRecyclerView.adapter = EditEquipmentsAdapter(this , mCamcodersList!!)
            bindingObj.editCamerasRecyclerView.adapter = EditEquipmentsAdapter(this , mCamerasList!!)

        }
    }

    override fun onUpdateProductOrEquipmentSuccess()
    {
        Toast.makeText(this , "Equipments has been updated successfully!" , Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onFailed(message: String)
    {

        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean)
    {
        if(visiblity)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
    }

    fun startProgressBarAnim()
    {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }

    fun showSnackbar(message: String)
    {
        try
        {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }


    override fun onClick(v: View?)
    {

        when(v?.id)
        {

            R.id.leftbtn->
            {
                finish()
            }

            R.id.submitEquipmentsBtn->
            {
                if(isNetworkActiveWithMessage())
                {
                    Timber.e("Products "+filter_products_list.toString().trim().replace(" " ,"").replace("[", "").replace("]","") )
                    Timber.e("Custom Products "+mCustomProductsList.toString().trim().replace(" " ,"").replace("[", "").replace("]","") )

                    mPresenter.updateproducts(sharedPreferences.getString(Constants.User_id).toString()
                            , filter_products_list.toString().trim().replace(" " ,"").replace("[", "").replace("]","")
                            , mCustomProductsList.toString().trim().replace(" " ,"").replace("[", "").replace("]","")
                    )
                }
            }

        }
    }
}
