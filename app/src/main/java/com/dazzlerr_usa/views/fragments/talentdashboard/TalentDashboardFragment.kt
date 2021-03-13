package com.dazzlerr_usa.views.fragments.talentdashboard

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentTalentDashboardBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.imageslider.EmojiRainLayout
import com.dazzlerr_usa.views.activities.UserInActiveActivity
import com.dazzlerr_usa.views.activities.becomefeatured.BecomeFeaturedActivity
import com.dazzlerr_usa.views.activities.emailverification.EmailVerificationModel
import com.dazzlerr_usa.views.activities.emailverification.EmailVerificationPresenter
import com.dazzlerr_usa.views.activities.emailverification.EmailVerificationView
import com.dazzlerr_usa.views.activities.emailverification.IndividualEmailVerificationActivity
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.messages.MessagesActivity
import com.dazzlerr_usa.views.activities.talentfollowers.TalentFollowersActivity
import com.dazzlerr_usa.views.activities.talentlikes.TalentLikesActivity
import com.dazzlerr_usa.views.activities.talentviews.TalentViewsActivity
import com.dazzlerr_usa.views.fragments.jobs.JobsFragment
import com.dazzlerr_usa.views.fragments.talentdashboard.adapters.TalentDashRecommendedJobsAdapter
import com.dazzlerr_usa.views.fragments.talentdashboard.adapters.TalentDashboardActivitiesAdapter
import com.dazzlerr_usa.views.fragments.talentdashboard.adapters.TalentDashboardAppliedJobsAdapter
import com.dazzlerr_usa.views.fragments.talentdashboard.adapters.TalentDashboardFeaturedJobsAdapter
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */

class TalentDashboardFragment : Fragment() , TalentDashboardView , View.OnClickListener, EmailVerificationView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj:FragmentTalentDashboardBinding
    lateinit var mPresenter: TalentDashboardPresenter
    lateinit var emailVerificationPresenter: EmailVerificationPresenter
     var emailVerificationDialog : Dialog ? =null
     var emailVerifyDialogProgressBar: SpinKitView? = null
     var is_user_featured = ""
     var featured_validity_date = ""
     var hasApiHit = false

    override fun onResume()
    {
        super.onResume()

        if(emailVerificationDialog!=null && emailVerificationDialog?.isShowing!!)
            emailVerificationDialog?.dismiss()
        apiCalling()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_talent_dashboard, container, false)
        initializations()
        clickListeners()
        animationSets()

        return bindingObj.root
    }

    fun initializations()
    {

        bindingObj.talentDashParentLayout.visibility = View.GONE

        (activity?.application as MyApplication).myComponent.inject(this)
        mPresenter = TalentDashboardModel(activity as Activity ,this)
        emailVerificationPresenter = EmailVerificationModel(activity as Activity ,this)

        (activity as HomeActivity).titleSettings(ContextCompat.getColor(context!!.applicationContext ,R.color.appColor), false, resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), "Dashboard", "", R.drawable.ic_notifications)
        (activity as HomeActivity).setBottomNavigationMenu(false, false, false, false, true)

        bindingObj.appliedJobsRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        bindingObj.topJobsRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        bindingObj.activitiesRecyclerview.layoutManager = LinearLayoutManager(activity)
        bindingObj.activitiesRecyclerview.addItemDecoration( DividerItemDecoration(bindingObj.activitiesRecyclerview.getContext(), DividerItemDecoration.VERTICAL))
        bindingObj.recommendedJobsRecycler.layoutManager = LinearLayoutManager(activity)
        bindingObj.recommendedJobsRecycler.addItemDecoration( DividerItemDecoration(bindingObj.activitiesRecyclerview.getContext(), DividerItemDecoration.VERTICAL))

        Glide.with(activity as Activity)
                .load("https://www.dazzlerr.com/API/"+sharedPreferences.getString(Constants.User_pic)).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(bindingObj.talentDashProfilePic)

        bindingObj.talentDashName.text = sharedPreferences.getString(Constants.User_name)
    }


    fun clickListeners()
    {
        bindingObj.talentDashMessagesLayout.setOnClickListener(this)
        bindingObj.talentDashAppliedJobsBtn.setOnClickListener(this)
        bindingObj.talentDashFeaturedJobsBtn.setOnClickListener(this)
        bindingObj.talentDashFollowersLayout.setOnClickListener(this)
        bindingObj.talentDashLikesLayout.setOnClickListener(this)
        bindingObj.talentDashViewsLayout.setOnClickListener(this)
        bindingObj.featuredBannerImg.setOnClickListener(this)
        bindingObj.weeklyBtn.setOnClickListener(this)
        bindingObj.monthlyBtn.setOnClickListener(this)
        bindingObj.allTimeBtn.setOnClickListener(this)
        bindingObj.talentDashRecommendedJobsBtn.setOnClickListener(this)
    }

    fun animationSets()
    {
        val likesAnim = AnimationUtils.loadAnimation(activity, R.anim.fade1)
        bindingObj.talentDashLikesLayout.startAnimation(likesAnim)
        bindingObj.talentDashFollowersLayout.startAnimation(likesAnim)
        bindingObj.talentDashViewsLayout.startAnimation(likesAnim)
        bindingObj.talentDashMessagesLayout.startAnimation(likesAnim)
    }

    private fun apiCalling()
    {
        if(activity?.isNetworkActive()!!)
        {
            mPresenter?.getTalentDashboard(sharedPreferences.getString(Constants.User_id).toString())
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

    fun fullDateFormatter(date : String)  :String
    {
        var dateStr = ""
        try {
            if (date!=null && date.isNotEmpty() && !date.equals("null")) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
                //val formatter = SimpleDateFormat("dd MMM, yyyy hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
                val formatter = SimpleDateFormat("dd MMM, yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
                dateStr = formatter.format(datetime)
                return dateStr
            }

        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }
            return dateStr
    }

    @SuppressLint("DefaultLocale")
    override fun onGetDashboardData(model: TalentDashboardPojo)
    {
        if(model.data?.size!=0)
        {
            if(model?.data?.get(0)?.is_active.equals("0"))
            {
                val bundle = Bundle()
                bundle.putString("title", "Account deactivated!")
                bundle.putString("message", "Sorry! Your account has been deactivated.")

                val intent = Intent(activity as Activity, UserInActiveActivity::class.java)

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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


            bindingObj.talentDashParentLayout.visibility = View.VISIBLE
            bindingObj.binderObj = model.data?.get(0)

            bindingObj.binderObj?.membership_start_date = fullDateFormatter(model.data?.get(0)?.membership_start_date.toString())
            bindingObj.binderObj?.membership_end_date = fullDateFormatter(model.data?.get(0)?.membership_end_date.toString())

            is_user_featured = model.data?.get(0)?.is_featured.toString()
            featured_validity_date = model.data?.get(0)?.featured_valid_till.toString()
            Constants.User_Messages_Count =  model.data?.get(0)?.messages!!

            if(model.data?.get(0)?.notification_count!!.isNotEmpty() && !model.data?.get(0)?.notification_count!!.equals("0")) {
                (activity as HomeActivity).binderObject.titleLayout.notificationCounttxt.text = if(model.data?.get(0)?.notification_count?.toInt()!! >9)"9+" else model.data?.get(0)?.notification_count!!
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

            sharedPreferences.saveString(Constants.Is_Featured, model.data?.get(0)?.is_featured.toString())
            sharedPreferences.saveString(Constants.Profile_Complete, model.data?.get(0)?.profile_complete.toString())
            sharedPreferences.saveString(Constants.Account_type, model.data?.get(0)?.member_type.toString())
            sharedPreferences.saveString(Constants.Membership_id, model.data?.get(0)?.membership_id.toString())
            sharedPreferences.saveString(Constants.Current_city, model.data?.get(0)?.current_city.toString())
            sharedPreferences.saveString(Constants.Current_state, model.data?.get(0)?.current_state.toString())
            sharedPreferences.saveString(Constants.IsProfile_published , ""+ model.data?.get(0)?.is_published)
            sharedPreferences.saveString(Constants.User_Secondary_Role , ""+ model.data?.get(0)?.secondary_role)



            Glide.with(activity as Activity)
                    .load("https://www.dazzlerr.com/API/"+model.data?.get(0)?.become_featured)
                    //.load("https://www.dazzlerr.com/API/"+"assets/app/membership/featured_banner.jpg")
                    .thumbnail(.2f)
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder2)
                    .into(bindingObj.featuredBannerImg)

            if(!model.data?.get(0)?.age.equals(""))
            {

                val arr = model.data?.get(0)?.age?.split("/")

                if (arr?.size == 3)
                {
                    bindingObj.binderObj?.age = getAge(year = arr[2].toInt(), month = arr[1].toInt(), day = arr[0].toInt()) + " Years"
                }
            }
            else
            {
                bindingObj.binderObj?.age = ""
            }

            bindingObj.binderObj?.member_type = model.data?.get(0)?.member_type?.capitalize()
            bindingObj.executePendingBindings()

            var strBuilder = StringBuilder()

            if(!bindingObj.binderObj?.role.equals(""))
                strBuilder.append(bindingObj.binderObj?.role)

            if(!bindingObj.binderObj?.secondary_role.equals(""))
                strBuilder.append(" | "+bindingObj.binderObj?.secondary_role)

            if(!bindingObj.binderObj?.age.equals(""))
                strBuilder.append("/ "+bindingObj.binderObj?.age)

            if(!bindingObj.binderObj?.gender.equals(""))
                strBuilder.append("/ "+bindingObj.binderObj?.gender?.capitalize())

            var strBuilder2 = StringBuilder()
            if(!bindingObj.binderObj?.current_city.equals(""))
                            strBuilder2.append(bindingObj.binderObj?.current_city)

            if(!bindingObj.binderObj?.current_state.equals("") && !bindingObj.binderObj?.current_state.equals(bindingObj.binderObj?.current_state))
                strBuilder2.append("/ "+bindingObj.binderObj?.current_state)


            bindingObj.talentDashUserStats.text = strBuilder
            bindingObj.talentDashUserAddress.text = strBuilder2


            if(!hasApiHit)
            {
                bindingObj.progressView1.progress = model.data?.get(0)?.profile_complete?.toFloat()!!
                bindingObj.progressView1.labelText = model.data?.get(0)?.profile_complete+"% completed"
                bindingObj.progressView1.progressAnimate()

                if(model.featured_jobs?.size!=0)
                    bindingObj.topJobsRecycler.adapter = TalentDashboardFeaturedJobsAdapter(activity as Activity, model.featured_jobs!!)
                else
                    bindingObj.talentDashFeaturedJobsLayout.visibility =View.GONE

            }
            // This is stoping views to animate when resume fragment
            hasApiHit  = true

            if(model.applied_jobs?.size!=0)
            bindingObj.appliedJobsRecycler.adapter = TalentDashboardAppliedJobsAdapter(activity as Activity, model.applied_jobs!!)
           else
                bindingObj.talentDashAppliedJobsLayout.visibility = View.GONE

            if(model.recommended_jobs?.size!=0)
            bindingObj.recommendedJobsRecycler.adapter = TalentDashRecommendedJobsAdapter(activity as Activity, model.recommended_jobs!!)
           else
                bindingObj.recommendedJobsLayout.visibility = View.GONE

            if(model.activity_log?.size!=0)
            bindingObj.activitiesRecyclerview.adapter = TalentDashboardActivitiesAdapter(activity as Activity, model.activity_log!!)

            else
                bindingObj.noActivityErrorTxt.visibility = View.VISIBLE
        }


        if(!model.data?.get(0)?.is_email_verified!!.equals("1", ignoreCase = true))
        {
            emailVerificationDialog()
        }

        if(activity?.isNetworkActiveWithMessage()!!)
        {
            mPresenter.getTalentActivitySummary(sharedPreferences.getString(Constants.User_id).toString() , "Weekly" , false)
        }
    }

    override fun onGetTalentActivitySummary(model: ActivitySummaryPojo, type: String)
    {

        if(type.equals("weekly" ,ignoreCase = true))
        {
            bindingObj.weeklyBtn.setBackgroundColor(activity?.resources!!.getColor(R.color.appColor))
            bindingObj.monthlyBtn.setBackgroundColor(activity?.resources!!.getColor(R.color.selectorGrey))
            bindingObj.allTimeBtn.setBackgroundColor(activity?.resources!!.getColor(R.color.selectorGrey))

            bindingObj.weeklyBtn.setTextColor(activity?.resources!!.getColor(R.color.colorWhite))
            bindingObj.monthlyBtn.setTextColor(activity?.resources!!.getColor(R.color.colorBlack))
            bindingObj.allTimeBtn.setTextColor(activity?.resources!!.getColor(R.color.colorBlack))

        }
        else if(type.equals("monthly" ,ignoreCase = true))
        {
            bindingObj.weeklyBtn.setBackgroundColor(activity?.resources!!.getColor(R.color.selectorGrey))
            bindingObj.monthlyBtn.setBackgroundColor(activity?.resources!!.getColor(R.color.appColor))
            bindingObj.allTimeBtn.setBackgroundColor(activity?.resources!!.getColor(R.color.selectorGrey))

            bindingObj.weeklyBtn.setTextColor(activity?.resources!!.getColor(R.color.colorBlack))
            bindingObj.monthlyBtn.setTextColor(activity?.resources!!.getColor(R.color.colorWhite))
            bindingObj.allTimeBtn.setTextColor(activity?.resources!!.getColor(R.color.colorBlack))

        }
        else
        {
            bindingObj.weeklyBtn.setBackgroundColor(activity?.resources!!.getColor(R.color.selectorGrey))
            bindingObj.monthlyBtn.setBackgroundColor(activity?.resources!!.getColor(R.color.selectorGrey))
            bindingObj.allTimeBtn.setBackgroundColor(activity?.resources!!.getColor(R.color.appColor))

            bindingObj.weeklyBtn.setTextColor(activity?.resources!!.getColor(R.color.colorBlack))
            bindingObj.monthlyBtn.setTextColor(activity?.resources!!.getColor(R.color.colorBlack))
            bindingObj.allTimeBtn.setTextColor(activity?.resources!!.getColor(R.color.colorWhite))

        }

        activitySummaryData(model?.data[0].job_count , model?.data[0].application_count , model?.data[0].contact_count , model?.data[0].portfolio_count ,model?.activity_level_title , model?.activity_level_suggestion , model?.monitor     )

    }

    fun activitySummaryData(jobApplies :String ,applicationView:String , contactViews :String , portfolioViews :String , activityLevelTitle : String , activityLevelDescription : String ,activityLevel: String )
    {
        bindingObj.talentDashJobAppliedTxt.text = jobApplies
        bindingObj.talentDashApplicationViewTxt.text = applicationView
        bindingObj.talentDashContactViewsTxt.text = contactViews
        bindingObj.talentDashPortfolioViewsTxt.text = portfolioViews
        bindingObj.activityLevelTitle.text = activityLevelTitle
        bindingObj.activityLevelDescription.text = activityLevelDescription

        if(activityLevel.equals("weak",ignoreCase = true))
        {
            bindingObj.weakLayout.alpha = 1f
            bindingObj.ExcellentLayout.alpha = 0.2f
            bindingObj.averageLayout.alpha = 0.2f
        }
        else if(activityLevel.equals("average",ignoreCase = true))
        {
            bindingObj.weakLayout.alpha = 0.2f
            bindingObj.ExcellentLayout.alpha = 0.2f
            bindingObj.averageLayout.alpha = 1f
        }
        else if(activityLevel.equals("excellent",ignoreCase = true))
        {
            bindingObj.weakLayout.alpha = 0.2f
            bindingObj.ExcellentLayout.alpha = 1f
            bindingObj.averageLayout.alpha = 0.2f
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

        try
        {
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
    private fun emailVerificationDialog()
    {
        emailVerificationDialog = Dialog(activity as Activity , R.style.NewDialogTheme)
        emailVerificationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        emailVerificationDialog?.setCancelable(false)
        emailVerificationDialog?.setContentView(R.layout.verify_email_dialog_layout)

        val dialogAlertEditTxt = emailVerificationDialog?.findViewById(R.id.dialogAlertEditTxt) as EditText
        val dialogPositiveBtn = emailVerificationDialog?.findViewById(R.id.dialogPositiveBtn) as Button
        val dialogNegativeBtn = emailVerificationDialog?.findViewById(R.id.dialogNegativeBtn) as Button
        emailVerifyDialogProgressBar = emailVerificationDialog?.findViewById(R.id.aviProgressBar) as SpinKitView

        dialogAlertEditTxt.setText(sharedPreferences.getString(Constants.User_Email))
        dialogAlertEditTxt.requestFocus()

        dialogPositiveBtn.setOnClickListener {

            emailVerificationPresenter.isEmailValidate(dialogAlertEditTxt.text.toString())
        }

        dialogNegativeBtn.setOnClickListener {
            emailVerificationDialog?.dismiss()
        }

        emailVerificationDialog?.show()
    }

    private fun verifyEmailApiCalling(email: String)
    {
        if(activity?.isNetworkActive()!!)
        {
            emailVerificationPresenter?.sendEmailOtp(sharedPreferences.getString(Constants.User_id).toString() , email)
        }

        else
        {

            val dialog = CustomDialog(activity as Activity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))

            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                verifyEmailApiCalling(email)
            })

            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    private fun getAge(year: Int, month: Int, day: Int): String? {
        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        dob.set(year, month, day)
        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        val ageInt = age
        return ageInt.toString()
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun onValidOtp()
    {
        //Not in use
    }

    override fun onValidEmail(email :String) {

        verifyEmailApiCalling(email)
    }

    override fun onOtpValidateSuccess() {

       //Not in use

    }

    override fun onOtpSentSuccess(email:String)
    {

        if(emailVerificationDialog!=null)
        emailVerificationDialog?.dismiss()

        val bundle = Bundle()
        bundle.putString("email" , email)
        val intent = Intent(activity as Activity , IndividualEmailVerificationActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun showProgress(visibility: Boolean)
    {

        if(activity !=null)
        {
            if (visibility)
            {
                (activity as HomeActivity).startProgressBarAnim()
            }
            else {
                (activity as HomeActivity).stopProgressBarAnim()
            }
        }

        if(emailVerificationDialog!=null&& emailVerifyDialogProgressBar!=null)
        {
            if(visibility)
                emailVerifyDialogProgressBar?.visibility = View.VISIBLE

            else
                emailVerifyDialogProgressBar?.visibility = View.GONE

        }
    }

    override fun showProgress(visibility: Boolean, sholdShowProgressBar: Boolean) {

        if(activity !=null)
        {
            if (visibility && sholdShowProgressBar)
            {
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

    override fun onClick(v: View?)
    {

        when(v?.id)
        {

            R.id.talentDashRecommendedJobsBtn->
            {

                var fragment = JobsFragment()
                (activity as HomeActivity).loadFirstFragment(fragment)
            }

            R.id.talentDashMessagesLayout->
            {
                val newIntent = Intent(activity as Activity, MessagesActivity::class.java)
                startActivity(newIntent)
            }

            R.id.featuredBannerImg->
            {

                if(is_user_featured.equals("1"))
                {
                    val mDialog = CustomDialog(activity )
                    mDialog.setTitle("Alert!")

                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(featured_validity_date)

                    if(featured_validity_date.isNotEmpty())
                    {
                        if(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(featured_validity_date).before( Date()))
                        {
                            val newIntent = Intent(activity as Activity, BecomeFeaturedActivity::class.java)
                            startActivity(newIntent)
                        }

                        else
                        {
                                mDialog.setMessage("You are already featured till " + simpledateFormat(featured_validity_date))
                            mDialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {
                                mDialog.dismiss()
                            })

                            mDialog.show()
                        }

                    }

                    else {
                        if (featured_validity_date.isEmpty())
                            mDialog.setMessage("You are already featured.")
                        else
                            mDialog.setMessage("You are already featured till " + simpledateFormat(featured_validity_date))
                        mDialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {
                            mDialog.dismiss()
                        })

                        mDialog.show()

                    }
                }
                else
                {
                    val newIntent = Intent(activity as Activity, BecomeFeaturedActivity::class.java)
                    startActivity(newIntent)
                }
            }

            R.id.talentDashFollowersLayout->
            {
                val newIntent = Intent(activity as Activity, TalentFollowersActivity::class.java)
                startActivity(newIntent)


            }

            R.id.talentDashLikesLayout->
            {
                val newIntent = Intent(activity as Activity, TalentLikesActivity::class.java)
                startActivity(newIntent)


            }

            R.id.talentDashViewsLayout->
            {
                val newIntent = Intent(activity as Activity, TalentViewsActivity::class.java)
                startActivity(newIntent)
            }

            R.id.talentDashAppliedJobsBtn->
            {
                val bundle = Bundle()
                bundle.putString("selectedTab" , "applied")
                var fragment = JobsFragment()
                fragment.arguments = bundle
                (activity as HomeActivity).loadFirstFragment(fragment)
            }

            R.id.talentDashFeaturedJobsBtn->
            {
                val bundle = Bundle()
                bundle.putString("selectedTab" , "featured")
                var fragment = JobsFragment()
                fragment.arguments = bundle
                (activity as HomeActivity).loadFirstFragment(fragment)
            }

            R.id.weeklyBtn->
            {

                if(activity?.isNetworkActiveWithMessage()!!)
                {
                    mPresenter.getTalentActivitySummary(sharedPreferences.getString(Constants.User_id).toString() , "weekly" ,true)
                }
            }

            R.id.monthlyBtn->
            {
                if(activity?.isNetworkActiveWithMessage()!!)
                {
                    mPresenter.getTalentActivitySummary(sharedPreferences.getString(Constants.User_id).toString() , "monthly" ,true)
                }
            }

            R.id.allTimeBtn->
            {
                if(activity?.isNetworkActiveWithMessage()!!)
                {
                    mPresenter.getTalentActivitySummary(sharedPreferences.getString(Constants.User_id).toString() , "" ,true)
                }
            }

        }
    }

    fun simpledateFormat(date : String): String
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("dd MMM yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
            var dateStr = ""

            val calendar = Calendar.getInstance()
            calendar.time = datetime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("hh:mma")

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Today " /*+ timeFormatter.format(datetime)*/
            }

            else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Yesterday "/* + timeFormatter.format(datetime)*/
            }

            else
            {
                dateStr = formatter.format(datetime)
            }

            dateStr = formatter.format(datetime)
            return dateStr
        }

        catch (e:Exception)
        {
            e.printStackTrace()
           return date
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode)
        {
            1111 -> apiCalling()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mPresenter.cancelRetrofitRequest()
    }

/*
    fun prepareNotification( title: String? ,  message : String? ,  intent: Intent, type: Int)
    {

        val pendingIntent = PendingIntent.getActivity(activity, ServiceUtility.randInt(0, 9999),
                intent, PendingIntent.FLAG_ONE_SHOT)



        val CHANNEL_ID = title
        val name = "Android"

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder = NotificationCompat.Builder(activity as Activity , CHANNEL_ID!!)

        mBuilder
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_dazzlerr)
                .setColor(resources.getColor(R.color.appColor))
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID!!)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)

        if(type==1)  //For BigPictureStyleNotification
            mBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bigImage))

        else if(type ==2)//For InboxStyleNotification
            mBuilder.setStyle(NotificationCompat.InboxStyle().addLine(message).setBigContentTitle(title))

        else if(type==3)//For BigTextStyleNotification
            mBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))

        val mNotificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            assert(mNotificationManager != null)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

        assert(mNotificationManager != null)
        mNotificationManager.notify(ServiceUtility.randInt(0, 9999999),
                mBuilder.build())

    }

    // Load bitmap from image url on background thread and display image notification
    private fun getBitmapAsyncAndDoWork(imageUrl: String ,title:String , message: String ,intent : Intent)
    {

        Glide.with(activity as HomeActivity)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap?>()
                {
                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?)
                    {
                        bigImage = resource
                        prepareNotification(title ,message , intent ,1)// 1 For BigPictureStyleNotification
                    }

                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                })

    }*/
}
