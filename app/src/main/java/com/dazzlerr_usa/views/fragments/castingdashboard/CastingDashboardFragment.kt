package com.dazzlerr_usa.views.fragments.castingdashboard


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentCastingDashboardBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.imageslider.EmojiRainLayout
import com.dazzlerr_usa.views.activities.UserInActiveActivity
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.dazzlerr_usa.views.activities.addjob.AddOrEditJobActivity
import com.dazzlerr_usa.views.activities.calllogs.CallLogsActivity
import com.dazzlerr_usa.views.activities.castingfollowings.CastingFollowingsActivity
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.interestedtalents.InterestedTalentsActivity
import com.dazzlerr_usa.views.activities.myJobs.MyJobsActivity
import com.dazzlerr_usa.views.activities.settings.SettingsActivity
import com.dazzlerr_usa.views.activities.shortlistedtalents.ShortListedTalentsActivity
import com.dazzlerr_usa.views.fragments.castingdashboard.adapters.CastingDashboardActiveJobsAdapter
import com.dazzlerr_usa.views.fragments.castingdashboard.adapters.DashboardInterestedTalentsAdapter
import com.dazzlerr_usa.views.fragments.castingdashboard.adapters.FeaturedTalentsAdapter
import com.dazzlerr_usa.views.fragments.castingdashboard.adapters.SearchByRoleAdapter
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */

class CastingDashboardFragment : Fragment(), CastingDashboardView, View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj: FragmentCastingDashboardBinding
    lateinit var mPresenter: CastingDashboardPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_casting_dashboard, container, false)
        initializations()
        clickListeners()
        animationSets()
        //apiCalling()
        return bindingObj.root
    }

    fun initializations()
    {

        bindingObj.castingDashParentLayout.visibility = View.GONE

        (activity?.application as MyApplication).myComponent.inject(this)
        Timber.e("membership id: "+ sharedPreferences.getString(Constants.Membership_id).toString())

        mPresenter = CastingDashboardModel(activity as Activity ,this)

        (activity as HomeActivity).titleSettings(ContextCompat.getColor(context!!.applicationContext ,R.color.appColor), false, resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), "Dashboard", "", R.drawable.ic_notifications)
        (activity as HomeActivity).setBottomNavigationMenu(false, false, false, false, true)

        bindingObj.activeJobsRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        bindingObj.featuredTalentsRecyclerview.layoutManager =  LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        bindingObj.interestedTalentsRecyclerview.layoutManager = LinearLayoutManager(activity)

        bindingObj.rolesRecyclerview.layoutManager = GridLayoutManager(activity ,3)

        bindingObj.interestedTalentsRecyclerview.addItemDecoration( DividerItemDecoration(bindingObj.interestedTalentsRecyclerview.getContext(), DividerItemDecoration.VERTICAL))

        Glide.with(activity as Activity)
                .load("https://www.dazzlerr.com/API/"+sharedPreferences.getString(Constants.User_pic)).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(bindingObj.castingDashProfilePic)

        bindingObj.castingDashName.text = sharedPreferences.getString(Constants.User_name)

       /* var strBuilder = StringBuilder()
        if(!sharedPreferences.getString(Constants.User_Role).equals(""))
            strBuilder.append(sharedPreferences.getString(Constants.User_Role)!!.capitalize())

        if(!sharedPreferences.getString(Constants.Current_city).equals(""))
            strBuilder.append("/ "+sharedPreferences.getString(Constants.Current_city))

        if(!sharedPreferences.getString(Constants.Current_state).equals(""))
            strBuilder.append(", "+sharedPreferences.getString(Constants.Current_state))


        bindingObj.castingDashUserStats.text = strBuilder*/
    }

    fun clickListeners()
    {
        bindingObj.castingDashMessagesLayout.setOnClickListener(this)
        bindingObj.castingDashAddJobBtn.setOnClickListener(this)
        bindingObj.castingDashMyJobsLayout.setOnClickListener(this)
        bindingObj.castingDashviewActiveJobsBtn.setOnClickListener(this)
        bindingObj.castingDashShortListedLayout.setOnClickListener(this)
        bindingObj.castingDashFollowingsLayout.setOnClickListener(this)
        bindingObj.castingDashViewAllTalentsBtn.setOnClickListener(this)
    }

    fun animationSets()
    {
        val likesAnim = AnimationUtils.loadAnimation(activity, R.anim.fade1)
        bindingObj.castingDashMyJobsLayout.startAnimation(likesAnim)
        bindingObj.castingDashShortListedLayout.startAnimation(likesAnim)
        bindingObj.castingDashFollowingsLayout.startAnimation(likesAnim)
        bindingObj.castingDashMessagesLayout.startAnimation(likesAnim)
    }

    private fun apiCalling()
    {

        if(activity?.isNetworkActive()!!)
        {
            mPresenter?.getCastingDashboard(sharedPreferences.getString(Constants.User_id).toString())
        }

        else
        {

            val dialog = CustomDialog(activity as Activity)
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

    override fun onGetDashboardData(model: CastingDashboardPojo)
    {

        if(model.data?.size!=0)
        {
            if(model?.data?.get(0)?.is_active.equals("0"))
            {
                val bundle = Bundle()
                bundle.putString("title", "Account deactivated!")
                bundle.putString("message", "Sorry! Your account has been deactivated.")

                val intent = Intent(activity as Activity, UserInActiveActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtras(bundle)
                startActivity(intent)
                (activity as Activity).finish()
            }

            if(model?.data?.get(0)?.is_notified.equals("0"))
            {

                if(model?.data?.get(0)?.personal_occasion?.isNotEmpty()!!)
                {
                    greetingDialog(model?.data?.get(0)?.personal_occasion!! ,model?.data?.get(0)?.personal_occasion_image!!)
                }

                if(model?.data?.get(0)?.festival_occasion?.isNotEmpty()!!)
                {
                    greetingDialog(model?.data?.get(0)?.festival_occasion!! ,model?.data?.get(0)?.festival_occasion_image!!)
                }

            }


            Constants.User_Messages_Count =  model.data?.get(0)?.messages!!


            if(model.data?.get(0)?.notification_count!!.isNotEmpty() && !model.data?.get(0)?.notification_count!!.equals("0")) {
                (activity as HomeActivity).binderObject.titleLayout.notificationCounttxt.text =  if(model.data?.get(0)?.notification_count?.toInt()!! >9)"9+" else model.data?.get(0)?.notification_count!!
                (activity as HomeActivity).binderObject.titleLayout.notificationCounttxt.visibility = View.VISIBLE
            }
            else
            {
                (activity as HomeActivity).binderObject.titleLayout.notificationCounttxt.visibility = View.GONE
            }


            if(model.data?.get(0)?.messages!!.isNotEmpty() && !model.data?.get(0)?.messages!!.equals("0")) {
                (activity as HomeActivity).binderObject.titleLayout.messageCounttxt.text = if(model.data?.get(0)?.messages?.toInt()!! >9)"9+" else model.data?.get(0)?.messages!!
                (activity as HomeActivity).binderObject.titleLayout.messageCounttxt.visibility = View.VISIBLE
            }
            else
            {
                (activity as HomeActivity).binderObject.titleLayout.messageCounttxt.visibility = View.GONE
            }

            sharedPreferences.saveString(Constants.CASTING_REPRESENTER, model.data?.get(0)?.representer!!)
            sharedPreferences.saveString(Constants.Current_state, model.data?.get(0)?.current_state.toString())
            sharedPreferences.saveString(Constants.IsProfile_published , ""+ model.data?.get(0)?.is_published)
            sharedPreferences.saveString(Constants.Is_Documement_Verified , ""+model.data?.get(0)?.is_document_verified) // 0 or 1 format
            sharedPreferences.saveString(Constants.Is_Documement_Submitted , ""+model.data?.get(0)?.is_document_submitted) // 0 or 1 format

            bindingObj.castingDashParentLayout.visibility = View.VISIBLE

            bindingObj.binderObj = model.data?.get(0)

            bindingObj.binderObj?.member_type = model.data?.get(0)?.member_type?.capitalize()
            bindingObj.executePendingBindings()

            if(bindingObj.binderObj?.company_name?.isNotEmpty()!!)
            bindingObj.castingDashUserCompanyname.text = bindingObj.binderObj?.company_name?.capitalize()

            else{
                bindingObj.castingDashUserCompanyname.visibility = View.GONE
            }

            var strBuilder = StringBuilder()
            if(!bindingObj.binderObj?.representer.equals(""))
                strBuilder.append( bindingObj.binderObj?.representer!!.capitalize())

            if(!bindingObj.binderObj?.current_state.equals(""))
                strBuilder.append(" / "+ bindingObj.binderObj?.current_state)

/*            if(!bindingObj.binderObj?.representer)
                strBuilder.append("/ "+ bindingObj.binderObj?.representer)*/

            bindingObj.castingDashUserStats.text = strBuilder

            bindingObj.featuredTalentsRecyclerview.adapter = FeaturedTalentsAdapter(activity as Activity, model.featured_talent!!)

            if(model.active_jobs?.size!=0)
                bindingObj.activeJobsRecycler.adapter = CastingDashboardActiveJobsAdapter(activity as Activity, model.active_jobs!!)
            else
                bindingObj.castingDashActiveJobsLayout.visibility = View.GONE


            if(model.intersted_talent?.size!=0)
                bindingObj.interestedTalentsRecyclerview.adapter = DashboardInterestedTalentsAdapter(activity as Activity, model.intersted_talent!!)
            else
                bindingObj.castingDashTalentsIntrestedJLayout.visibility =View.GONE


            bindingObj.rolesRecyclerview.adapter = SearchByRoleAdapter(activity as Activity, model.category_roles!!)


        }

    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun showProgress(visibility: Boolean)
    {

        if(activity !=null)
        {
            if (visibility){
                (activity as HomeActivity).startProgressBarAnim()
            }
            else {
                (activity as HomeActivity).stopProgressBarAnim()
            }
        }
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = (activity as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    fun greetingDialog(greetingMessageTxt: String , greetingImageTxt: String)
    {
        var mDialog = Dialog(activity as Activity, R.style.dialogAnimationTransition /*android.R.style.Theme_Black_NoTitleBar_Fullscreen*/)
        mDialog.setContentView(R.layout.occasional_greeting_dialog)
        mDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)

        var greetingTitle = mDialog.findViewById<TextView>(R.id.greetingTitle)
        var greetingMessage = mDialog.findViewById<TextView>(R.id.greetingMessage)
        var greetingImage = mDialog.findViewById<ImageView>(R.id.greetingImage)
        var greetingCloseBtn = mDialog.findViewById<ImageView>(R.id.greetingCloseBtn)
        var greetingCommonBackgroundImage = mDialog.findViewById<ImageView>(R.id.greetingCommonBackgroundImage)
        var group_emoji_container = mDialog.findViewById<EmojiRainLayout>(R.id.group_emoji_container)

        val text = "<b><font color=#000000>Dazzlerr</font></b> <font color=#000000> wishing you very</font>"
        greetingTitle.text = Html.fromHtml(text)
        greetingMessage.text = greetingMessageTxt

        if(greetingMessageTxt.toLowerCase()?.contains("birthday") || greetingMessageTxt.toLowerCase()?.contains("anniversary"))
        {

            group_emoji_container?.addEmoji(R.drawable.occasional_cake)
            group_emoji_container.addEmoji(R.drawable.occasional_confetti)
            group_emoji_container.addEmoji(R.drawable.occasional_balloons)
        }

        else
        {
            group_emoji_container.addEmoji(R.drawable.occasional_confetti)
            group_emoji_container.addEmoji(R.drawable.occasional_balloons)
        }


        try{
        group_emoji_container.startDropping()
        }
        catch(e:Exception)
        {
            e.printStackTrace()
        }
        if(greetingImageTxt.isNotEmpty())
        {
            Glide.with(activity as Activity)
                    .load("https://www.dazzlerr.com/API/" + greetingImageTxt).apply(RequestOptions().centerCrop())
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(greetingImage)
        }

        else
        {
            greetingImage.visibility = View.GONE
            greetingCommonBackgroundImage.visibility  = View.VISIBLE
        }
        greetingCloseBtn.setOnClickListener {
            mDialog.dismiss()
        }
        mDialog.show()
    }


    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.castingDashMessagesLayout->
            {
               // val newIntent = Intent(activity as Activity, MessagesActivity::class.java)
                val newIntent = Intent(activity as Activity, CallLogsActivity::class.java)
                startActivity(newIntent)
            }

            R.id.castingDashMyJobsLayout->
            {
                startActivity(Intent(activity as Activity , MyJobsActivity::class.java))
            }

            R.id.castingDashShortListedLayout->
            {
                startActivity(Intent(activity as Activity , ShortListedTalentsActivity::class.java))
            }

            R.id.castingDashViewAllTalentsBtn->
            {
                startActivity(Intent(activity as Activity , InterestedTalentsActivity::class.java))
            }

            R.id.castingDashFollowingsLayout->
            {
                startActivity(Intent(activity as Activity , CastingFollowingsActivity::class.java))
            }

            R.id.castingDashviewActiveJobsBtn->
            {
                val bundle = Bundle()
                bundle.putString("selectedTab" , "active")
                startActivity(Intent(activity as Activity , MyJobsActivity::class.java).putExtras(bundle))
            }

            R.id.castingDashAddJobBtn->
            {
                if(sharedPreferences.getString(Constants.IsProfile_published).equals("1"))
                {
                    if (sharedPreferences.getString(Constants.Is_Phone_Verified).equals("1") || sharedPreferences.getString(Constants.Is_Email_Verified).equals("1")) {

                        if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("1", ignoreCase = true)
                                && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true)) {
                            val bundle = Bundle()
                            bundle.putString("type", "Add")
                            bundle.putString("call_id", "")
                            val intent = Intent(activity as Activity, AddOrEditJobActivity::class.java)
                            intent.putExtras(bundle)
                            startActivityForResult(intent, 101)
                        } else if (sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("0", ignoreCase = true)
                                || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("", ignoreCase = true)
                                || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("null", ignoreCase = true)) {

                            val dialog = CustomDialog(activity as Activity)
                            dialog.setMessage("Your identity is not verified. It is very important to help protect our community and ensure that Dazzlerr stays safe and secure. We verify identity in several ways:\n" +
                                    "\n" +
                                    "- Government ID Verification.\n" +
                                    "- Video Verification.")

                            dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {

                                dialog.dismiss()
                                val bundle = Bundle()
                                bundle.putString("type", "documentVerification")
                                val newIntent = Intent(activity as Activity, AccountVerification::class.java)
                                newIntent.putExtras(bundle)
                                startActivity(newIntent)
                            })

                            dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                                dialog.dismiss()
                            })

                            dialog.show()


                        } else if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("0", ignoreCase = true)
                                && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true)) {


                            val dialog = CustomDialog(activity as Activity)
                            dialog.setMessage("Your identity verification is under process. It will take 24-48 hours.")
                            dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {
                                dialog.dismiss()
                            })

                            dialog.show()
                        }
                    } else {
                        val dialog = CustomDialog(activity as Activity)
                        dialog.setMessage("Your email and mobile number is not verified. Please verify your details to proceed.")
                        dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

                            val bundle = Bundle()
                            bundle.putString("type", "emailOrPhoneVerification")
                            val newIntent = Intent(activity as Activity, AccountVerification::class.java)
                            newIntent.putExtras(bundle)
                            startActivity(newIntent)
                        })

                        dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                            dialog.dismiss()
                        })
                        dialog.show()
                    }
                }

                else
                {
                    val dialog = CustomDialog(activity)
                    dialog.setTitle(activity?.resources?.getString(R.string.published_error_title))
                    dialog.setMessage(activity?.resources?.getString(R.string.published_error_message))
                    dialog.setPositiveButton("SETTINGS", DialogListenerInterface.onPositiveClickListener {

                        val newIntent = Intent(activity, SettingsActivity::class.java)
                        startActivity(newIntent)
                    })

                    dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
                }
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==101)
        {
            //apiCalling()
        }
    }
    override fun onDestroy() {
        super.onDestroy()

        mPresenter.cancelRetrofitRequest()
    }

    override fun onResume() {
        super.onResume()
        apiCalling()
    }
}
