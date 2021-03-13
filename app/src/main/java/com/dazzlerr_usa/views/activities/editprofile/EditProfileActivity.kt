package com.dazzlerr_usa.views.activities.editprofile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEditProfileBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.editprofile.models.ProfileStatsModel
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProfileStatsPojo
import com.dazzlerr_usa.views.activities.editprofile.presenters.ProfileStatsPresenter
import com.dazzlerr_usa.views.activities.editprofile.views.ProfileStatsView
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.activities.settings.SettingsActivity
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class EditProfileActivity : AppCompatActivity() , View.OnClickListener , ProfileStatsView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj: ActivityEditProfileBinding
    lateinit var mPresenter : ProfileStatsPresenter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj  =DataBindingUtil.setContentView(this ,R.layout.activity_edit_profile)
        initializations()
        clickListeners()
        //apiCalling()
    }

    fun initializations()
    {

        mPresenter = ProfileStatsModel(this , this)
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.setText("Edit Profile")
        bindingObj.mainLayout.visibility = View.GONE
        if(sharedPreferences.getString(Constants.User_Role).equals("Model",  ignoreCase = true) || sharedPreferences.getString(Constants.User_Role).equals("Actor",  ignoreCase = true) )
            bindingObj.editAppearanceBtn.visibility = View.VISIBLE
        else
            bindingObj.editAppearanceBtn.visibility = View.GONE


        if(sharedPreferences.getString(Constants.User_Role).equals("Photographer",  ignoreCase = true) )
        {
            bindingObj.editServicesBtn.visibility = View.VISIBLE


            //bindingObj.editProductBtn.visibility = View.VISIBLE

            //Hiding products now 08/09/2020
            bindingObj.editProductBtn.visibility = View.GONE
            bindingObj.editProductTxt.text = "Equipments We Use"
            bindingObj.editProductImage.setImageResource(R.drawable.ic_equipment_we_use)
            bindingObj.editTeamBtn.visibility = View.GONE //Removing Edit team button for now
        }

        else if(sharedPreferences.getString(Constants.User_Role).equals("Makeup Artist",  ignoreCase = true))
        {

            //bindingObj.editServicesBtn.visibility = View.GONE

            //Showing services now 08/09/2020
            bindingObj.editServicesBtn.visibility = View.VISIBLE


            //bindingObj.editProductBtn.visibility = View.VISIBLE

            //Hiding products now 08/09/2020
            bindingObj.editProductBtn.visibility = View.GONE

            bindingObj.editProductTxt.text = "Products We Use"
            bindingObj.editProductImage.setImageResource(R.drawable.ic_product_we_use)
            bindingObj.editTeamBtn.visibility = View.GONE//Removing Edit team button for now
        }

        else
        {
            bindingObj.editTeamBtn.visibility = View.GONE//Removing Edit team button for now
            bindingObj.editServicesBtn.visibility = View.GONE
            bindingObj.editProductBtn.visibility = View.GONE
        }

    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.editGeneralInformationBtn.setOnClickListener(this)
        bindingObj.editAppearanceBtn.setOnClickListener(this)
        bindingObj.editRatesBtn.setOnClickListener(this)
        bindingObj.editImagesBtn.setOnClickListener(this)
        bindingObj.editVideosBtn.setOnClickListener(this)
        bindingObj.editAudiosBtn.setOnClickListener(this)
        bindingObj.editProjectsBtn.setOnClickListener(this)
        bindingObj.editTeamBtn.setOnClickListener(this)
        bindingObj.editProductBtn.setOnClickListener(this)
        bindingObj.editServicesBtn.setOnClickListener(this)
        bindingObj.editSecurityQuestionBtn.setOnClickListener(this)
        bindingObj.editProfilePicBtn.setOnClickListener(this)
    }


    private fun apiCalling()
    {

        if(this@EditProfileActivity?.isNetworkActive()!!)
        {
            mPresenter.getProfileStats(sharedPreferences.getString(Constants.User_id).toString())
        }

        else
        {

            val dialog = CustomDialog(this@EditProfileActivity)
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


    override fun onSuccess(model: ProfileStatsPojo)
    {
        bindingObj.mainLayout.visibility = View.VISIBLE
        bindingObj.binderObj = model
        bindingObj.executePendingBindings()

        sharedPreferences.saveString(Constants.Profile_Complete, model.total.toString())

    }

    override fun onFailed(message: String)
    {

        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean) {

            if (visiblity) {
                startProgressBarAnim()
            } else {
                stopProgressBarAnim()
            }

    }

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }

    fun showSnackbar(message: String)
    {
        try {
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
            R.id.leftbtn-> {
                setResult(100)
                finish()
            }

            R.id.editGeneralInformationBtn->
            {
                val intent = Intent(this@EditProfileActivity, EditGeneralInformationActivity::class.java)
                startActivity(intent)
            }

            R.id.editAppearanceBtn->
            {
                val intent = Intent(this@EditProfileActivity, EditAppearanceActivity::class.java)
                startActivity(intent)
            }

            R.id.editRatesBtn->
            {
                val intent = Intent(this@EditProfileActivity, EditRatesAndTravelActivity::class.java)
                startActivity(intent)
            }

            R.id.editImagesBtn->
            {
                val bundle  = Bundle()
                bundle.putString("from" , intent.extras!!.getString("from"))
                bundle.putString("user_id" , intent.extras?.getString("user_id"))
                bundle.putString("shouldShowProjects" , "true")
                bundle.putInt("currentTab" , 1) //1 for images, 2 for videos, 3 for audios and 4 for projects
                val intent = Intent(this@EditProfileActivity, PortfolioActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            R.id.editAudiosBtn->
            {
                val bundle  = Bundle()
                bundle.putString("from" , intent.extras!!.getString("from"))
                bundle.putString("user_id" , intent.extras?.getString("user_id"))
                bundle.putString("shouldShowProjects" , "true")
                bundle.putInt("currentTab" , 3) //1 for images, 2 for videos, 3 for audios and 4 for projects
                val intent = Intent(this@EditProfileActivity, PortfolioActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            R.id.editProjectsBtn->
            {
                val bundle  = Bundle()
                bundle.putString("from" , intent.extras!!.getString("from"))
                bundle.putString("user_id" , intent.extras?.getString("user_id"))
                bundle.putString("shouldShowProjects" , "true")
                bundle.putInt("currentTab" , 4) //1 for images, 2 for videos, 3 for audios and 4 for projects
                val intent = Intent(this@EditProfileActivity, PortfolioActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            R.id.editVideosBtn->
            {
                val bundle  = Bundle()
                bundle.putString("from" , intent.extras!!.getString("from"))
                bundle.putString("user_id" , intent.extras?.getString("user_id"))
                bundle.putString("shouldShowProjects" , "true")
                bundle.putInt("currentTab" , 2) //1 for images, 2 for videos, 3 for audios and 4 for projects
                val intent = Intent(this@EditProfileActivity, PortfolioActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            R.id.editServicesBtn->
            {
                val intent = Intent(this@EditProfileActivity, EditServicesActivity::class.java)
                startActivity(intent)
            }

            R.id.editTeamBtn->
            {
                val intent = Intent(this@EditProfileActivity, EditTeamActivity::class.java)
                startActivity(intent)
            }

            R.id.editProductBtn->
            {
                var intent : Intent?= null
                if(sharedPreferences.getString(Constants.User_Role).equals("Photographer",  ignoreCase = true) )
                 intent = Intent(this@EditProfileActivity, EditEquipmentsActivity::class.java)

                else intent = Intent(this@EditProfileActivity, EditProductsActivity::class.java)

                startActivity(intent)
            }

            R.id.editSecurityQuestionBtn->
            {
                val bundle = Bundle()
                bundle.putBoolean("fromEditProfile" ,true)
                val newIntent = Intent(this@EditProfileActivity, SettingsActivity::class.java)
                newIntent.putExtras(bundle)
                startActivity(newIntent)

            }

            R.id.editProfilePicBtn->
            {
                val newIntent = Intent(this@EditProfileActivity, EditProfileImageActivity::class.java)
                startActivity(newIntent)
            }
        }
    }


    override fun onResume() {
        super.onResume()

        apiCalling()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(100)
        finish()
    }
}
