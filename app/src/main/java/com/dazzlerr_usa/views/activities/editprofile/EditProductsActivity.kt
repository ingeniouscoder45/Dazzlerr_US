package com.dazzlerr_usa.views.activities.editprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEditProductsBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.editprofile.adapters.EditProductsAdapter
import com.dazzlerr_usa.views.activities.editprofile.models.ProductsAndEquipmentsModel
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProductsAndEquipmentsPojo
import com.dazzlerr_usa.views.activities.editprofile.presenters.ProductsAndEquipmentsPresenter
import com.dazzlerr_usa.views.activities.editprofile.views.ProductsAndEquipmentsView
import com.dazzlerr_usa.views.activities.editprofile.adapters.SkillsAndInterestsAdapter
import com.google.android.flexbox.*
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class EditProductsActivity : AppCompatActivity(), View.OnClickListener , ProductsAndEquipmentsView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj : ActivityEditProductsBinding
    lateinit var mPresenter: ProductsAndEquipmentsPresenter
    var filter_products_list  : ArrayList<String> = ArrayList()
    var mCustomProductsList : MutableList<String> = ArrayList()
    var mCustomProductsAdapter: SkillsAndInterestsAdapter?=null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_edit_products)
        initializations()
        clicklisteners()
        apiCalling()
    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Products"
        mPresenter = ProductsAndEquipmentsModel(this, this)

        val productsLayoutManager = FlexboxLayoutManager(this)
        productsLayoutManager.flexDirection = FlexDirection.ROW
        productsLayoutManager.justifyContent = JustifyContent.FLEX_START
        productsLayoutManager.alignItems= AlignItems.STRETCH
        productsLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.customProductsRecycler.setLayoutManager(productsLayoutManager)
        mCustomProductsAdapter = SkillsAndInterestsAdapter(this, mCustomProductsList)
        bindingObj.customProductsRecycler.adapter = mCustomProductsAdapter

    }

    fun clicklisteners()
    {

        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.addProductsBtn.setOnClickListener(this)
        bindingObj.submitProductsBtn.setOnClickListener(this)

    }

    private fun apiCalling()
    {

        if(this@EditProductsActivity?.isNetworkActive()!!)
        {
            mPresenter.getProdutsOrEquipments(sharedPreferences.getString(Constants.User_id).toString())
        }

        else
        {

            val dialog = CustomDialog(this@EditProductsActivity)
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
            val gManager = GridLayoutManager(this, 2)
            bindingObj.productsRecyclerView.setLayoutManager(gManager)
            bindingObj.productsRecyclerView.adapter = EditProductsAdapter(this , model?.role_products!!)
        }
    }

    override fun onUpdateProductOrEquipmentSuccess()
    {
        Toast.makeText(this , "Products has been updated successfully!" , Toast.LENGTH_SHORT).show()
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

            R.id.addProductsBtn->
            {
                if(bindingObj.customProductsEdittext.text.length!=0)
                {
                    mCustomProductsList.add(bindingObj.customProductsEdittext.text.toString())
                    mCustomProductsAdapter?.notifyDataSetChanged()
                    bindingObj.customProductsEdittext.text.clear()
                }
            }

            R.id.submitProductsBtn->
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