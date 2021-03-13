package com.dazzlerr_usa.views.activities.editprofile

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEditAppearanceBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.editprofile.presenters.AppearancePresenter
import com.dazzlerr_usa.views.activities.editprofile.views.AppearanceView
import com.dazzlerr_usa.views.activities.editprofile.adapters.SkillsAndInterestsAdapter
import com.dazzlerr_usa.views.fragments.profile.ProfileModel
import com.dazzlerr_usa.views.fragments.profile.ProfilePojo
import com.dazzlerr_usa.views.fragments.profile.ProfilePresenter
import com.dazzlerr_usa.views.fragments.profile.ProfileView
import com.google.android.flexbox.*
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener
import com.dazzlerr_usa.views.fragments.profile.pojo.GetContactDetailsPojo
import com.onlinepoundstore.fragments.login.AppearanceModel
import kotlinx.android.synthetic.main.fragment_appearance.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class EditAppearanceActivity : AppCompatActivity(),View.OnClickListener , AppearanceView,ProfileView
{

    override fun onShortList(status: String) {
        //Not in use
    }

    override fun onFollowOrUnfollow(status: String) {
        //Not in use
    }

    override fun onLikeOrDislike(status: String) {
        //Not in use
    }
    override fun onGetContactDetails(model: GetContactDetailsPojo) {
        //Not in use
    }

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj: ActivityEditAppearanceBinding
    lateinit var mPresenter: AppearancePresenter
    lateinit var profilePresenter : ProfilePresenter
    var mTagsList : MutableList<String> = ArrayList()
    lateinit var tagsAdapter: SkillsAndInterestsAdapter
    var haircolor = ""
    var hairlength = ""
    var hairtype = ""
    var eyecolor = ""
    var skintone = ""
    var dresssize = ""
    var shoessize = ""
    lateinit var profilePojo: ProfilePojo

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj  = DataBindingUtil.setContentView(this ,R.layout.activity_edit_appearance)
        initializations()
        clickListeners()
    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Appearance"
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        mPresenter = AppearanceModel(this , this)
        profilePresenter = ProfileModel(this as Activity , this)

        if(isNetworkActiveWithMessage())
            profilePresenter.getProfile(sharedPreferences.getString(Constants.User_id).toString() ,"")
    }

    fun clickListeners()
    {
        bindingObj.appearanceSubmitBtn.setOnClickListener(this)

        bindingObj.appearanceHairColorBtn.setOnClickListener(this)
        bindingObj.appearanceHairLengthBtn.setOnClickListener(this)
        bindingObj.appearanceHairTypeBtn.setOnClickListener(this)
        bindingObj.appearanceEyeColorBtn.setOnClickListener(this)
        bindingObj.appearanceSkinToneBtn.setOnClickListener(this)
        bindingObj.appearanceShoesBtn.setOnClickListener(this)

        bindingObj.appearanceAddTagsBtn.setOnClickListener(this)
        bindingObj.appearanceFeetpickerBtn.setOnClickListener(this)
        bindingObj.appearanceInchpickerBtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }

    fun tagsDataPopulate()
    {

        if(profilePojo.data?.get(0)?.tags!=null && !profilePojo.data?.get(0)?.tags.equals(""))
            mTagsList  = profilePojo.data?.get(0)?.tags?.split((Regex("\\n|,"))) as MutableList<String>

        if(mTagsList.size==1)
        {
            val value = mTagsList.get(0)
            mTagsList =ArrayList()
            mTagsList.add(value)
        }

        val interestLayoutManager = FlexboxLayoutManager(this)
        interestLayoutManager.flexDirection = FlexDirection.ROW
        interestLayoutManager.justifyContent = JustifyContent.FLEX_START
        interestLayoutManager.alignItems= AlignItems.STRETCH
        interestLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.appearanceTagsRecyclerView.setLayoutManager(interestLayoutManager)
        tagsAdapter = SkillsAndInterestsAdapter(this, mTagsList)
        bindingObj.appearanceTagsRecyclerView.adapter = tagsAdapter
    }

    fun populateProfileData()
    {

        if(profilePojo.data?.get(0)?.height!!.length>=3 && profilePojo.data?.get(0)?.height!!.contains(".")) {
            bindingObj.appearanceFeetTxt.setText(profilePojo.data?.get(0)?.height.toString().split(".").get(0))
            bindingObj.appearanceInchTxt.setText(profilePojo.data?.get(0)?.height.toString().split(".").get(1))
        }

        bindingObj.appearanceWeightEdittext.setText(profilePojo.data?.get(0)?.weight)
        bindingObj.appearanceBicepsEdittext.setText(profilePojo.data?.get(0)?.biceps)
        bindingObj.appearanceChestEdittext.setText(profilePojo.data?.get(0)?.chest)
        bindingObj.appearanceWaistEdittext.setText(profilePojo.data?.get(0)?.waist)
        bindingObj.appearanceHipsEdittext.setText(profilePojo.data?.get(0)?.hips)

        //Disabling biceps option for female
        bindingObj.appearanceBicepsEdittext.isEnabled = !profilePojo.data?.get(0)!!.gender.equals("female",ignoreCase = true)
    }

    fun hairColorPicker()
    {
        //---------------Populating data on Hair color spinner
        val hairColorArray = resources.getStringArray(R.array.haircolor_array)
        var position = 0
        for(i in hairColorArray.indices)
        {
            if(profilePojo.data?.get(0)?.hair_color.equals(hairColorArray[i] , ignoreCase = true)) {
                position = i
            }
        }


        val hairColorPicker = ItemPickerDialog(this@EditAppearanceActivity, hairColorArray.toCollection(ArrayList()), position)
        hairColorPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
            override fun onItemSelected(position: Int, selectedValue: String) {

                haircolor = hairColorArray[position]
                bindingObj.appearanceHairColorTxt.text = haircolor
                hairColorPicker.dismiss()
            }
        })
        hairColorPicker.show()


    }


    fun hairLengthPicker()
    {
        //---------------Populating data on Hair length spinner

        val hairLengthArray = resources.getStringArray(R.array.hairlength_array)

        var position = 0
        for(i in hairLengthArray.indices)
        {
            if(profilePojo.data?.get(0)?.hair_length.equals(hairLengthArray[i] , ignoreCase = true)) {
                position = i
            }
        }


        val hairLengthPicker = ItemPickerDialog(this@EditAppearanceActivity, hairLengthArray.toCollection(ArrayList()), position)
        hairLengthPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {

                hairlength = hairLengthArray[position]
                bindingObj.appearanceHairLengthTxt.text = hairlength
                hairLengthPicker.dismiss()
            }
        })

        hairLengthPicker.show()

    }

    fun hairTypePicker()
    {

        //---------------Populating data on Hair type spinner

        val hairTypeArray = resources.getStringArray(R.array.hairtype_array)

        var position = 0
        for(i in hairTypeArray.indices)
        {

            if(profilePojo.data?.get(0)?.hair_type.equals(hairTypeArray[i] , ignoreCase = true))
            {
                position = i
            }
        }


        val hairTypePicker = ItemPickerDialog(this@EditAppearanceActivity, hairTypeArray.toCollection(ArrayList()), position)
        hairTypePicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
            override fun onItemSelected(position: Int, selectedValue: String)
            {
                hairtype = hairTypeArray[position]
                bindingObj.appearanceHairTypeTxt.text = hairtype
                hairTypePicker.dismiss()
            }
        })

        hairTypePicker.show()

    }

    fun eyeColorPicker()
    {
        //---------------Populating data on eye color spinner
        val eyeColorArray = resources.getStringArray(R.array.eyecolor_array)

        var position = 0
        for(i in eyeColorArray.indices)
        {

            if(profilePojo.data?.get(0)?.eye_color.equals(eyeColorArray[i] , ignoreCase = true))
            {
                position = i
            }
        }


        val eyeColorPicker = ItemPickerDialog(this@EditAppearanceActivity, eyeColorArray.toCollection(ArrayList()), position)
        eyeColorPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
            override fun onItemSelected(position: Int, selectedValue: String) {

                eyecolor = eyeColorArray[position]
                bindingObj.appearanceEyeColorTxt.text = eyecolor
                eyeColorPicker.dismiss()
            }
        })

        eyeColorPicker.show()

    }

    fun skinTonePicker()
    {
        //---------------Populating data on skin tone spinner

        val skinToneArray = resources.getStringArray(R.array.skintone_array)

        var position = 0
        for(i in skinToneArray.indices)
        {

            if(profilePojo.data?.get(0)?.skintone.equals(skinToneArray[i] , ignoreCase = true))
            {
                position = i

            }
        }

        val skinTonePicker = ItemPickerDialog(this@EditAppearanceActivity, skinToneArray.toCollection(ArrayList()), position)
        skinTonePicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {
                skintone = skinToneArray[position]
                bindingObj.appearanceSkinToneTxt.text = skintone
                skinTonePicker.dismiss()
            }
        })

        skinTonePicker.show()

    }

    fun shoeSizePicker()
    {
        //---------------Populating data on shoes size spinner

        val shoeSizeArray = resources.getStringArray(R.array.shoessize_array)
        var position = 0

        for(i in shoeSizeArray.indices)
        {

            if(profilePojo.data?.get(0)?.shoes.equals(shoeSizeArray[i] , ignoreCase = true))
            {
                position = i
            }
        }

        val shoesSizePicker = ItemPickerDialog(this@EditAppearanceActivity, shoeSizeArray.toCollection(ArrayList()), position)
        shoesSizePicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {

                shoessize = shoeSizeArray[position]
                bindingObj.appearanceShoesTxt.text = shoessize
                shoesSizePicker.dismiss()
            }
        })

        shoesSizePicker.show()

    }
    fun populateSpinnerData()
    {

        bindingObj.appearanceHairColorTxt.text = profilePojo.data?.get(0)?.hair_color
        bindingObj.appearanceHairLengthTxt.text = profilePojo.data?.get(0)?.hair_length
        bindingObj.appearanceHairTypeTxt.text = profilePojo.data?.get(0)?.hair_type
        bindingObj.appearanceEyeColorTxt.text = profilePojo.data?.get(0)?.eye_color
        bindingObj.appearanceSkinToneTxt.text = profilePojo.data?.get(0)?.skintone
        bindingObj.appearanceShoesTxt.text = profilePojo.data?.get(0)?.shoes

        haircolor = profilePojo.data?.get(0)?.hair_color!!
        hairlength = profilePojo.data?.get(0)?.hair_length!!
        hairtype = profilePojo.data?.get(0)?.hair_type!!
        eyecolor = profilePojo.data?.get(0)?.eye_color!!
        skintone = profilePojo.data?.get(0)?.skintone!!
        shoessize = profilePojo.data?.get(0)?.shoes!!

        //---------------Populating data on dress size spinner

        bindingObj.appearanceDressSpinner.setItems(getResources().getStringArray(R.array.dresssize_array))

        if(profilePojo.data?.get(0)?.dress!=null && !profilePojo.data?.get(0)?.dress.equals(""))
        {
            var mDressList: MutableList<String>
            mDressList = profilePojo.data?.get(0)?.dress?.split((Regex("\\n|,"))) as MutableList<String>

            if (mDressList.size == 1)
            {
                val value = mDressList.get(0)
                mDressList = ArrayList()
                mDressList.add(value)
            }

            bindingObj.appearanceDressSpinner.setSelectionlist(mDressList)
        }

    }

    fun heightFeetPopupmenu()
    {

        val arrayList :ArrayList<String> = ArrayList()
        for(i:Int in 1..7)
        {
            arrayList.add(""+i)
        }

        val itemPickerDialog = ItemPickerDialog(this@EditAppearanceActivity ,arrayList ,0 )
        itemPickerDialog.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {
                bindingObj.appearanceFeetTxt.text = arrayList[position]
                itemPickerDialog.dismiss()
            }
        })

        itemPickerDialog.show()
    }


    fun heightInchPopupmenu()
    {

        val arrayList :ArrayList<String> = ArrayList()
        for(i:Int in 0..11)
        {
            arrayList.add(""+i)
        }

        val itemPickerDialog = ItemPickerDialog(this@EditAppearanceActivity ,arrayList ,0 )
        itemPickerDialog.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {
                bindingObj.appearanceInchTxt.text = arrayList[position]
                itemPickerDialog.dismiss()
            }
        })

        itemPickerDialog.show()

    }

    override fun onGetProfileSuccess(model: ProfilePojo) {
        profilePojo = model

        populateProfileData()
        tagsDataPopulate()
        populateSpinnerData()
    }

    override fun onSuccess()
    {
        Toast.makeText(this , "Appearance has been updated successfully" , Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onProfilePicUploaded(url: String) {
        //--- Not in use
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun onProfileFailed(message: String) {
        showSnackbar(message)
    }

    fun showSnackbar(message : String)
    {
        Snackbar.make(findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
    }

    override fun showProgress(showProgress: Boolean)
    {
        if(showProgress)
            startProgressBarAnim()

        else
           stopProgressBarAnim()
    }



    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {

        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }
    override fun onClick(v: View?)
    {
        when(v?.id)
        {
            R.id.leftbtn -> finish()

            R.id.appearanceFeetpickerBtn-> heightFeetPopupmenu()
            R.id.appearanceInchpickerBtn-> heightInchPopupmenu()

            R.id.appearanceAddTagsBtn->
            {
                if(bindingObj.appearanceTagsEdittext.text.length!=0)
                {
                    mTagsList.add(bindingObj.appearanceTagsEdittext.text.toString())
                    tagsAdapter.notifyDataSetChanged()
                    bindingObj.appearanceTagsEdittext.text.clear()
                }
            }

            R.id.appearanceSubmitBtn->
            {

                mPresenter.updateAppearance(sharedPreferences.getString(Constants.User_id).toString()
                        ,appearanceFeetTxt.text.toString().trim()+"."+appearanceInchTxt.text.toString().trim()
                        , appearanceWeightEdittext.text.toString().trim()
                        , appearanceBicepsEdittext.text.toString().trim()
                        , appearanceChestEdittext.text.toString().trim()
                        , appearanceWaistEdittext.text.toString().trim()
                        , appearanceHipsEdittext.text.toString().trim()
                        , haircolor
                        , hairlength
                        , hairtype
                        , eyecolor
                        , skintone
                        , bindingObj.appearanceDressSpinner.selectedItemsAsString
                        , shoessize
                        , mTagsList.toString().replace("[" ,"").replace("]" , ""))
            }

            R.id.appearanceHairColorBtn->
            {
                hairColorPicker()
            }

            R.id.appearanceHairLengthBtn->
            {
                hairLengthPicker()
            }

            R.id.appearanceHairTypeBtn->
            {
                hairTypePicker()
            }

            R.id.appearanceEyeColorBtn->
            {
                eyeColorPicker()
            }

            R.id.appearanceSkinToneBtn->
            {
                skinTonePicker()
            }

            R.id.appearanceShoesBtn->
            {
                shoeSizePicker()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
