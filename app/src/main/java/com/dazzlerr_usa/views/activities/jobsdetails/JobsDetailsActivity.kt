package com.dazzlerr_usa.views.activities.jobsdetails


import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityJobsDetailsBinding
import com.dazzlerr_usa.extensions.capitalizeWords
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.customdialogs.ProfileCompletenessAlertDialog
import com.dazzlerr_usa.utilities.customdialogs.ProfileCompletenessListener.onNegativeClickListener
import com.dazzlerr_usa.utilities.customdialogs.ProfileCompletenessListener.onPositiveClickListener
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.dazzlerr_usa.views.activities.editprofile.EditProfileActivity
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.activities.mymembership.MyMembershipActivity
import com.dazzlerr_usa.views.activities.settings.SettingsActivity
import com.dazzlerr_usa.views.fragments.home.pojos.loadModelImage
import com.google.android.flexbox.*
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class JobsDetailsActivity : AppCompatActivity() , JobDetailsView , View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj: ActivityJobsDetailsBinding
    lateinit var mPresenter: JobDetailsPresenter
    lateinit var mDialog:Dialog
    lateinit var adapter:CastingProposalsAdapter

    var is_featured:String = "0"
    var can_apply:String? = "1"
    var job_apply_count:String? = ""
    var profile_complete:String? = "0"
    var call_id= ""
    var job_link= ""
    var phoneNumberString: String =""
    var emailString : String =""
    var jobShortlistStatus = "0"
    lateinit  var proposalShortlistBtn : Button
    lateinit  var proposalHireBtn : Button
    lateinit  var proposalRejectBtn : Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_jobs_details)
        initializations()
        apiCalling()
        clickListeners()
    }

    fun initializations()
    {
        //----------------Injecting dagger into sharedepreferences
        (application as MyApplication).myComponent.inject(this)

        showAds()

        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.titletxt.text = "Job Details"
        bindingObj.titleLayout.titletxt.visibility = View.VISIBLE
        bindingObj.jobDetailsParentLayout.visibility  =View.GONE
        mPresenter = JobDetailsModel(this , this)

        call_id = intent.extras!!.getString("call_id","")


        val interestLayoutManager = FlexboxLayoutManager(this)
        interestLayoutManager.flexDirection = FlexDirection.ROW
        interestLayoutManager.justifyContent = JustifyContent.FLEX_START
        interestLayoutManager.alignItems= AlignItems.STRETCH
        interestLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.tagsRecyclerView.setLayoutManager(interestLayoutManager)

        if(sharedPreferences.getString(Constants.User_Role).equals("casting" , ignoreCase = true))
        {
            bindingObj.sendMessageBtn.visibility = View.GONE
            bindingObj.sendMessageBtn2.visibility = View.GONE
            bindingObj.contactLayout.visibility = View.GONE
            bindingObj.shortlistJobBtn.visibility = View.GONE
        }
        else
        {
            bindingObj.sendMessageBtn.visibility = View.VISIBLE
            bindingObj.sendMessageBtn2.visibility = View.VISIBLE
            bindingObj.contactLayout.visibility = View.VISIBLE
            bindingObj.shortlistJobBtn.visibility = View.VISIBLE
        }
    }

    fun showAds()
    {

/*
        val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
        val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
*/

        if(sharedPreferences.getString(Constants.Membership_id).equals("")
                ||sharedPreferences.getString(Constants.Membership_id).equals("0")
                ||sharedPreferences.getString(Constants.Membership_id).equals("1")
                ||sharedPreferences.getString(Constants.Membership_id).equals("6")
        ) {
            //MobileAds.initialize(this) {}
            val adRequest = AdRequest.Builder().build()
            bindingObj.adView.visibility = View.VISIBLE
            bindingObj.adView.loadAd(adRequest)
        }
    }
    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightLayout.setOnClickListener(this)
        bindingObj.sendMessageBtn.setOnClickListener(this)
        bindingObj.sendMessageBtn2.setOnClickListener(this)
        bindingObj.getContactDetailsBtn.setOnClickListener(this)
        bindingObj.shortlistJobBtn.setOnClickListener(this)
    }
    private fun apiCalling()
    {
        if(isNetworkActive()!!)
        {
            mPresenter.getjobDetails(call_id , sharedPreferences.getString(Constants.User_id).toString() , sharedPreferences.getString(Constants.Membership_id).toString() )
        }
        else
        {

            val dialog  =  CustomDialog(this)
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

    override fun onSuccess(model: JobDetailsPojo)
    {

        if(model.data?.size!=0)
        {

            bindingObj.jobDetailsParentLayout.visibility  =View.VISIBLE
            bindingObj.jobdetailsBinder = model.data?.get(0)

            if( model.data?.get(0)?.is_job_shortlisted.equals("1"))
            {
                updateShortlistJobStatus(model.data?.get(0)?.is_job_shortlisted.toString())
            }


            if( model.data?.get(0)?.job_role!!.isNotEmpty())
            bindingObj.jobdetailsBinder?.job_role = model.data?.get(0)?.job_role!!.capitalizeWords()

            bindingObj.jobdetailsBinder?.gender = model.data?.get(0)?.gender?.capitalize()

            if( model.data?.get(0)?.budget_duration!!.isNotEmpty())
            bindingObj.jobdetailsBinder?.budget_duration = model.data?.get(0)?.budget_duration!!.capitalizeWords()

            if(model.data?.get(0)?.budget_amount!!.isEmpty())
            {
                bindingObj.jobdetailsBinder?.budget = "As Per Profile"
            }

            else
            {
                if(Constants.CURRENT_CURRENCY.isEmpty() || Constants.CURRENT_CURRENCY.equals("inr",ignoreCase = true))
                bindingObj.jobdetailsBinder?.budget = Constants.RupeesSign+""+model.data?.get(0)?.budget_amount?.capitalize()

                else
                    bindingObj.jobdetailsBinder?.budget = Constants.DollorSign+""+model.data?.get(0)?.budget_amount?.capitalize()
            }


            bindingObj.executePendingBindings()

            job_link = model.data?.get(0)?.job_link.toString()
            is_featured = model.data?.get(0)?.is_featured.toString()
            can_apply = model.data?.get(0)?.can_apply.toString()
            job_apply_count = model.data?.get(0)?.job_apply_count.toString()
            profile_complete = model.data?.get(0)?.profile_complete.toString()

            castingSettings(model?.data?.get(0)?.casting_id.toString())

            if(model?.data?.get(0)?.is_applied.equals("1"))
            {
            bindingObj.sendMessageBtn.text = "Applied"
            bindingObj.sendMessageBtn2.text = "Applied"
            bindingObj.sendMessageBtn.isEnabled = false
            bindingObj.sendMessageBtn2.isEnabled = false
            bindingObj.sendMessageBtn.isClickable = false
            bindingObj.sendMessageBtn2.isClickable = false

            bindingObj.replyLayout.visibility = View.VISIBLE

                bindingObj.currentuserName.text  = sharedPreferences.getString(Constants.User_name)
                val imageUrl = sharedPreferences.getString(Constants.User_pic)
                Glide.with(this@JobsDetailsActivity)
                        .load("https://www.dazzlerr.com/API/"+imageUrl).apply(RequestOptions().centerCrop())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(bindingObj.userImage)

                if(model?.data?.get(0)?.casting_reply.equals(""))
                {
                    bindingObj.castingReplyPersonName.visibility = View.GONE
                    bindingObj.castingReplyPersonName1.visibility = View.GONE
                    bindingObj.castingReplyLayout.visibility = View.GONE
                    bindingObj.talentPurposalStatus.visibility = View.GONE
                    bindingObj.castingReply.visibility = View.VISIBLE
                    bindingObj.castingReply.text = "No reply found yet!"
                }

                else
                {

                    bindingObj.castingReplyLayout.visibility = View.VISIBLE
                    bindingObj.castingReplyPersonName.visibility = View.VISIBLE
                    bindingObj.castingReplyPersonName1.visibility = View.VISIBLE
                    bindingObj.castingReply.visibility = View.VISIBLE

                    model?.data?.get(0)?.applied_date?.let { simpledateFormat(bindingObj.appliedDateTxt , it) }

                    bindingObj.castingReplyPersonName.text = if(!model?.data?.get(0)?.contact_person.equals("")) model?.data?.get(0)?.contact_person else model?.data?.get(0)?.name


                    if(model?.data?.get(0)?.casting_msg?.contains("1",ignoreCase = true)!!)
                    {
                        bindingObj.talentPurposalStatus.visibility = View.VISIBLE
                        bindingObj.talentPurposalStatus.text = "Hired"
                        bindingObj.talentPurposalStatus.setBackgroundColor(resources.getColor(R.color.colorGreen))
                    }
                    else if(model?.data?.get(0)?.casting_msg?.contains("0",ignoreCase = true)!!)
                    {
                        bindingObj.talentPurposalStatus.visibility = View.VISIBLE
                        bindingObj.talentPurposalStatus.text = "Rejected"
                        bindingObj.talentPurposalStatus.setBackgroundColor(resources.getColor(R.color.appColor))
                    }
                    else if(model?.data?.get(0)?.proposal_shortlist_status.equals("1"))
                    {
                        bindingObj.talentPurposalStatus.visibility = View.VISIBLE
                        bindingObj.talentPurposalStatus.text = "Shortlisted"
                        bindingObj.talentPurposalStatus.setBackgroundColor(resources.getColor(R.color.skyBlueColor))
                    }
                    else
                    {

                            bindingObj.talentPurposalStatus.visibility = View.GONE

                    }
                }

                bindingObj.castingReplyLayout.setOnClickListener {

                    val bundle = Bundle()
                    bundle.putString("user_id" , ""+model?.data?.get(0)?.casting_id)
                    bundle.putString("user_role" , "casting")
                    bundle.putString("is_featured" , "0")
                    startActivity(Intent(this@JobsDetailsActivity, OthersProfileActivity::class.java).putExtras(bundle))
                }
            }
            else
            {
                bindingObj.sendMessageBtn.text = "Apply Now"
                bindingObj.sendMessageBtn2.text = "Apply Now"
                bindingObj.sendMessageBtn.isEnabled = true
                bindingObj.sendMessageBtn2.isEnabled = true
                bindingObj.sendMessageBtn.isClickable = true
                bindingObj.sendMessageBtn2.isClickable = true
                bindingObj.replyLayout.visibility = View.GONE
            }

            if(model?.data?.get(0)?.contact_email.equals("") || model?.data?.get(0)?.contact_email.equals("null"))
                emailString =  model?.data?.get(0)?.email.toString()

            else
                emailString = model.data?.get(0)?.contact_email.toString()

            if(model?.data?.get(0)?.contact_mobile.equals("") || model?.data?.get(0)?.contact_mobile.equals("null"))
                phoneNumberString =  model?.data?.get(0)?.phone.toString()

            else
                phoneNumberString = model.data?.get(0)?.contact_mobile.toString()

            if(model?.data?.get(0)?.contact_person.equals("") || model?.data?.get(0)?.contact_person.equals("null"))
            bindingObj.jobDetailsNameTxt.text = model.data?.get(0)?.name.toString()

            else
            bindingObj.jobDetailsNameTxt.text = model.data?.get(0)?.contact_person.toString()


            if(model?.data?.get(0)?.contact_person.equals("Dazzlerr Team" ,ignoreCase = true))
                bindingObj.jobDetailsNameTxt.text = "-"

            if(model?.data?.get(0)?.representer.equals("") || model?.data?.get(0)?.representer.equals("null"))
             bindingObj.jobDetailsRepresenterTxt.text = "Casting"

            else if(model?.data?.get(0)?.representer==null)
            {
                bindingObj.jobDetailsRepresenterTxt.text = "Casting"
            }
            else
                bindingObj.jobDetailsRepresenterTxt.text  =  model.data?.get(0)?.representer.toString()

            bindingObj.jobDetailsViewsTxt.text = " "+model.data?.get(0)?.views.toString()

            if(model.data?.get(0)?.profile_pic.toString().equals("") || model.data?.get(0)?.profile_pic.toString().equals("null"))
                bindingObj.profileImage.setImageResource(R.drawable.casting_director)
                else
            loadModelImage(bindingObj.profileImage  ,model.data?.get(0)?.profile_pic.toString() )

            dateFormat(bindingObj.expiryDate , model.data?.get(0)?.job_expiry.toString() )
            dateFormat(bindingObj.startDate , model.data?.get(0)?.start_date.toString() )
            dateFormat(bindingObj.endDate , model.data?.get(0)?.end_date.toString() )

            if(model.data?.get(0)?.audition.equals("1"))
                bindingObj.auditionRequiredTxt.text = "Yes"
            else
                bindingObj.auditionRequiredTxt.text = "No"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                bindingObj.jobDescription.setText(Html.fromHtml( model.data?.get(0)?.description, Html.FROM_HTML_MODE_LEGACY));
            }

            else
            {
                bindingObj.jobDescription.setText(Html.fromHtml(model.data?.get(0)?.description));
            }
            var mTagsList : MutableList<String> = ArrayList()

            if(model.data?.get(0)?.tags!=null && !model.data?.get(0)?.tags.equals(""))
                mTagsList  = model.data?.get(0)?.tags?.split(",") as MutableList<String>

            bindingObj.tagsRecyclerView.adapter  = TagsAdapter(this , mTagsList)
            if(mTagsList.size==0) {
                bindingObj.tagsRecyclerView.visibility = View.GONE
                bindingObj.noTagsFoundTxt.visibility = View.VISIBLE
            }

            else {
                bindingObj.tagsRecyclerView.visibility = View.VISIBLE
                bindingObj.noTagsFoundTxt.visibility = View.GONE
            }

        }
    }

    override fun onGetProposals(model: GetProposalsPojo)
    {

        if(model?.data?.size!=0)
        {
            bindingObj.proposalsRecyclerView.visibility = View.VISIBLE
            bindingObj.noPorposalsFoundTxt.visibility = View.GONE
            adapter = CastingProposalsAdapter(this, model?.data!!)
            val gManager = LinearLayoutManager(this)
            bindingObj.proposalsRecyclerView.layoutManager = gManager
            bindingObj.proposalsRecyclerView.addItemDecoration(DividerItemDecoration(bindingObj.proposalsRecyclerView.getContext(), DividerItemDecoration.VERTICAL))
            bindingObj.proposalsRecyclerView.adapter = adapter
        }
        else
        {
            bindingObj.proposalsRecyclerView.visibility = View.GONE
            bindingObj.noPorposalsFoundTxt.visibility = View.VISIBLE
        }
    }

    override fun onPurposalShortlisted(status: String ,position: Int)
    {
        try
        {
            if(adapter?.mModelList?.size!!>position)
                adapter?.mModelList?.get(position)?.status = status
            adapter?.notifyItemChanged(position)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

        if(status.equals("0"))
            Toast.makeText(this@JobsDetailsActivity , "Proposal has been removed from shortlisted proposal list" , Toast.LENGTH_LONG).show()
            else if(status.equals("1"))
            Toast.makeText(this@JobsDetailsActivity , "Proposal has been added to your shortlisted proposal list" , Toast.LENGTH_LONG).show()

        updateShortlistPurposalStatus(status ,position)
    }

    override fun onApplicationViewed() {

    }
    fun shortlistPurposal(creply_id :String, status: String ,position : Int )
    {
        mPresenter.shortListPurposal(creply_id ,status ,position)
    }

    override fun onJobShortlisted(status: String)
    {
        updateShortlistJobStatus(status)
    }

    private fun updateShortlistJobStatus(status : String)
    {
        jobShortlistStatus = status

        if(status.equals("1")) {

            bindingObj.shortlistJobBtn.text = "Shortlisted"
            bindingObj.shortlistJobBtn.setBackgroundColor(resources.getColor(R.color.selectorGrey))
            bindingObj.shortlistJobBtn.setTextColor(resources.getColor(R.color.black))

        }
        else if(status.equals("0"))
        {
            bindingObj.shortlistJobBtn.text = "Shortlist"
            bindingObj.shortlistJobBtn.setBackgroundColor(resources.getColor(R.color.appColor))
            bindingObj.shortlistJobBtn.setTextColor(resources.getColor(R.color.colorWhite))
        }
    }
    override fun onHiredOrRejected(request_sent: String,position : Int) {

        try
        {
            if(adapter?.mModelList?.size!!>position)
                adapter?.mModelList?.get(position)?.request_sent = request_sent
            adapter?.notifyItemChanged(position)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

        updateHireAndRejectStatus(request_sent.toString() , position)
    }



    fun purposalsDialog(model: GetProposalsPojo.DataBean , position : Int)
    {
        var mDialog = Dialog(this, R.style.dialogAnimationTransition /*android.R.style.Theme_Black_NoTitleBar_Fullscreen*/)
        mDialog.setContentView(R.layout.purposals_dialog_layout)
        mDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)

        var purposalsUsernameTxt = mDialog.findViewById<TextView>(R.id.purposalUsernameTxt)
        var proposalsMainLayout = mDialog.findViewById<LinearLayout>(R.id.proposalsMainLayout)
        var purposalsUserroleTxt= mDialog.findViewById<TextView>(R.id.purposalUserroleTxt)
        var purposalDateTxt= mDialog.findViewById<TextView>(R.id.purposalDateTxt)
        var purposalDescriptionTxt= mDialog.findViewById<TextView>(R.id.purposalDescriptionTxt)
        var leftbtn= mDialog.findViewById<ImageView>(R.id.leftbtn)
        var userImage= mDialog.findViewById<ImageView>(R.id.userImage)

        Glide.with(this)
                .load("https://www.dazzlerr.com/API/" + model.profile_pic).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(userImage)

        proposalShortlistBtn= mDialog.findViewById<Button>(R.id.proposalShortlistBtn)
        proposalHireBtn= mDialog.findViewById<Button>(R.id.proposalHireBtn)
        proposalRejectBtn= mDialog.findViewById<Button>(R.id.proposalRejectBtn)

        purposalsUsernameTxt.setText(model.name)
        purposalsUserroleTxt.setText(model.user_role)
        purposalDescriptionTxt.setText(model.msg)
        simpledateFormat(purposalDateTxt , model.created_on.toString())

        updateShortlistPurposalStatus(model.status.toString() ,position)
        updateHireAndRejectStatus(model.request_sent.toString() ,position)

        leftbtn.setOnClickListener {
            mDialog.dismiss()
        }

        if(isNetworkActiveWithMessage())
            mPresenter.viewApplication(call_id   , sharedPreferences.getString(Constants.User_id.toString()).toString(), model.user_id.toString())
        proposalShortlistBtn.setOnClickListener {

            if(model.status.equals("0") || model.status.equals(""))
                shortlistPurposal(model.creply_id.toString() , "1" , position )

            else if(model.status.equals("1"))
               shortlistPurposal(model.creply_id.toString() , "0"  , position)
        }


        proposalHireBtn.setOnClickListener {

            if(model.request_sent.equals("0")|| model.request_sent.equals(""))
                hireOrRejectDialog(model.creply_id.toString() , "1"  , "hire" ,position)

            else
                showSnackbar("Already hired!")
        }

        proposalRejectBtn.setOnClickListener {

            if(model.request_sent.equals("1")|| model.request_sent.equals(""))
                hireOrRejectDialog(model.creply_id.toString() , "0"  , "reject" ,position)

            else
                showSnackbar("Already Rejected!")
        }
        proposalsMainLayout.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("user_id" , ""+model.user_id)
            bundle.putString("user_role" , ""+model.user_role)
            bundle.putString("is_featured" , ""+model.is_featured)
            startActivity(Intent(this@JobsDetailsActivity, OthersProfileActivity::class.java).putExtras(bundle))
        }

        mDialog.show()

    }

    fun askCastingChecks(model: GetProposalsPojo.DataBean , position : Int)
    {
        if (sharedPreferences.getString(Constants.User_Role).equals("casting", ignoreCase = true)) {

            if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("1", ignoreCase = true)
                    && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true))
            {

                purposalsDialog(model ,position)

            } else if (sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("0", ignoreCase = true)
                    || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("", ignoreCase = true)
                    || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("null", ignoreCase = true)) {
                val dialog = CustomDialog(this@JobsDetailsActivity)
                dialog.setMessage("Your identity is not verified. It is very important to help protect our community and ensure that Dazzlerr stays safe and secure. We verify identity in several ways:\n" +
                        "\n" +
                        "- Government ID Verification.\n" +
                        "- Video Verification.")
                dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {
                    dialog.dismiss()
                    val bundle = Bundle()
                    bundle.putString("type", "documentVerification")
                    val newIntent = Intent(this@JobsDetailsActivity, AccountVerification::class.java)
                    newIntent.putExtras(bundle)
                    startActivity(newIntent)
                })
                dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                    dialog.dismiss()
                })

                dialog.show()

            } else if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("0", ignoreCase = true)
                    && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true)) {
                val dialog = CustomDialog(this@JobsDetailsActivity)
                dialog.setMessage("Your identity verification is under process. It will take 24-48 hours.")
                dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {
                    dialog.dismiss()
                })

                dialog.show()
            }


        }

    }

    fun simpledateFormat(view: TextView, date : String)
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("MMM dd, yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
            var dateStr = ""

            val calendar = Calendar.getInstance()
            calendar.time = datetime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("hh:mma")

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Today " + timeFormatter.format(datetime)
            }
            else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Yesterday " + timeFormatter.format(datetime)
            }
            else
            {
                dateStr = formatter.format(datetime)
            }
            view.text = dateStr
        }

        catch (e:Exception)
        {
            e.printStackTrace()
            view.text = date
        }

    }

    fun updateShortlistPurposalStatus(status :String , position: Int)
    {

        if(status.equals("0") || status.equals(""))
        {
            proposalShortlistBtn.text = "Shortlist"
            proposalShortlistBtn.setBackgroundResource(R.drawable.edittext_round_border1)
            proposalShortlistBtn.setTextColor(resources.getColor(R.color.appColor))
        }

        else if(status.equals("1"))
        {
            proposalShortlistBtn.text = "Shortlisted"
            proposalShortlistBtn.setBackgroundResource(R.drawable.edittext_round_border2)
            proposalShortlistBtn.setTextColor(resources.getColor(R.color.colorWhite))

        }

    }

    fun updateHireAndRejectStatus(request_sent :String  , position : Int)
    {
        if(request_sent.equals("") || request_sent.equals("null"))
        {
            proposalHireBtn.text = "Hire"
            proposalHireBtn.setBackgroundResource(R.drawable.edittext_round_border1)
            proposalHireBtn.setTextColor(resources.getColor(R.color.appColor))

            proposalRejectBtn.text = "Reject"
            proposalRejectBtn.setBackgroundResource(R.drawable.edittext_round_border1)
            proposalRejectBtn.setTextColor(resources.getColor(R.color.appColor))

        }

        else if(request_sent.equals("0"))
        {
            proposalRejectBtn.text = "Rejected"
            proposalRejectBtn.setBackgroundResource(R.drawable.edittext_round_border2)
            proposalRejectBtn.setTextColor(resources.getColor(R.color.colorWhite))
            proposalHireBtn.text = "Hire"
            proposalHireBtn.setBackgroundResource(R.drawable.edittext_round_border1)
            proposalHireBtn.setTextColor(resources.getColor(R.color.appColor))

        }

        else if(request_sent.equals("1"))
        {
            proposalHireBtn.text = "Hired"
            proposalHireBtn.setBackgroundResource(R.drawable.edittext_round_border3)
            proposalHireBtn.setTextColor(resources.getColor(R.color.colorWhite))

            proposalRejectBtn.text = "Reject"
            proposalRejectBtn.setBackgroundResource(R.drawable.edittext_round_border1)
            proposalRejectBtn.setTextColor(resources.getColor(R.color.appColor))

        }
    }
    fun castingSettings(casting_id:String)
    {
        if(sharedPreferences.getString(Constants.User_Role).toString().equals("casting" ,ignoreCase = true) &&  casting_id.equals(sharedPreferences.getString(Constants.User_id)))
        {
            bindingObj.castingPorposalsLayout.visibility = View.VISIBLE

            bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("   All   "))
            bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("   Hired   "))
            bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("   Rejected   "))
            bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("   Shortlisted   "))

            bindingObj.tabLayout.getTabAt(0)?.select()
            mPresenter.getjobProposals(intent.extras!!.getString("call_id","") , sharedPreferences.getString(Constants.User_id).toString(), "all")


            bindingObj.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
            {
                override fun onTabSelected(tab: TabLayout.Tab)
                {
                    if(tab.text.toString().trim().equals("All", ignoreCase = true))
                    {


                        mPresenter.getjobProposals(intent.extras!!.getString("call_id","") , sharedPreferences.getString(Constants.User_id).toString(), "all")

                    }

                    else if(tab.text.toString().trim().equals("Hired", ignoreCase = true))
                    {


                        mPresenter.getjobProposals(intent.extras!!.getString("call_id","") , sharedPreferences.getString(Constants.User_id).toString(), "hired")
                    }

                    else if(tab.text.toString().trim().equals("Rejected", ignoreCase = true))
                    {


                        mPresenter.getjobProposals(intent.extras!!.getString("call_id","") , sharedPreferences.getString(Constants.User_id).toString(), "rejected")

                    }

                    else if(tab.text.toString().trim().equals("Shortlisted", ignoreCase = true))
                    {

                        mPresenter.getjobProposals(intent.extras!!.getString("call_id","") , sharedPreferences.getString(Constants.User_id).toString(), "shortlisted")

                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab)
                {

                }

                override fun onTabReselected(tab: TabLayout.Tab)
                {

                }
            })

        }
        else
            bindingObj.castingPorposalsLayout.visibility = View.GONE

    }

    override fun onFailed(message: String)
    {

        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean) {

        if(visiblity)
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

            R.id.shortlistJobBtn ->
            {
                if(isUserLogined() && isNetworkActiveWithMessage())
                {
                    if(jobShortlistStatus.equals("1"))
                    mPresenter.shortList_job(sharedPreferences.getString(Constants.User_id).toString() , call_id ,"0" )

                    else
                        mPresenter.shortList_job(sharedPreferences.getString(Constants.User_id).toString() , call_id ,"1" )

                }
            }
            R.id.getContactDetailsBtn->
            {
                if (isUserLogined() && isNetworkActiveWithMessage())
                {
                    if(sharedPreferences.getString(Constants.Membership_id).toString().equals("0"))
                    {
                        val dialog = CustomDialog(this@JobsDetailsActivity)
                        dialog.setTitle("Alert!")
                        dialog.setMessage("This feature is only available to premium or elite members, please purchase a membership.")
                        dialog.setPositiveButton("Buy now", DialogListenerInterface.onPositiveClickListener {
                            var bundle = Bundle()
                            bundle.putString("membership_type" , "upgrade")

                            startActivityForResult(Intent(this@JobsDetailsActivity  , MyMembershipActivity::class.java).putExtras(bundle) ,100)
                        })

                        dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                            dialog.dismiss()
                        })
                        dialog.show()
                    }

                    else if(sharedPreferences.getString(Constants.Membership_id).toString().equals("1"))
                    {
                        val dialog = CustomDialog(this@JobsDetailsActivity)
                        dialog.setTitle("Alert!")
                        dialog.setMessage("This feature is only available to premium or elite members, please upgrade your membership.")
                        dialog.setPositiveButton("Buy now", DialogListenerInterface.onPositiveClickListener {
                            var bundle = Bundle()
                            bundle.putString("membership_type" , "upgrade")

                            startActivityForResult(Intent(this@JobsDetailsActivity  , MyMembershipActivity::class.java).putExtras(bundle) ,100)
                        })

                        dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                            dialog.dismiss()
                        })
                        dialog.show()
                    }
                    else {
                        if(isNetworkActiveWithMessage())
                        mPresenter.getJobContact(bindingObj.jobdetailsBinder?.call_id?.toString()!!, sharedPreferences.getString(Constants.User_id).toString())
                    }
                }
            }

            R.id.sendMessageBtn->
            {
                if(isNetworkActiveWithMessage())
                applyNow()
            }

            R.id.sendMessageBtn2->
            {
                if(isNetworkActiveWithMessage())
                applyNow()
            }

            R.id.rightLayout->
            {
                if(!job_link.equals(""))
                {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, job_link)

                        // (Optional) Here we're setting the title of the content
                        putExtra(Intent.EXTRA_TITLE, "Dazzlerr")
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Dazzlerr - Connecting Talent")
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }
        }
    }

    fun applyNow()
    {
        if(isUserLogined()) {

            if (sharedPreferences.getString(Constants.IsProfile_published).equals("1"))
            {
                if (sharedPreferences.getString(Constants.Is_Phone_Verified).equals("1") || sharedPreferences.getString(Constants.Is_Email_Verified).equals("1")) {

/*                    if (is_featured.equals("1"))
                    {

                        if (profile_complete!!.toInt() < 60)
                        {

                            val mDialog = ProfileCompletenessAlertDialog(this@JobsDetailsActivity)
                            mDialog.setprogressValue(profile_complete!!.toInt())
                            mDialog.setMessage("Your profile should be completed at least 60% to apply on any job. Please complete your profile first to enjoy the benefits.")
                            mDialog.setPositiveButton("Complete Now", object : onPositiveClickListener {
                                override fun onPositiveClick() {
                                    val bundle = Bundle()
                                    bundle.putString("from", "jobsDetails")
                                    bundle.putString("type", "profileComplete")
                                    bundle.putString("user_id", sharedPreferences.getString(Constants.User_id))
                                    val intent = Intent(this@JobsDetailsActivity, EditProfileActivity::class.java)
                                    intent.putExtras(bundle)
                                    startActivityForResult(intent, 100)
                                    mDialog.dismiss()
                                }
                            })

                            mDialog.setNegativeButton("I'll complete it later", object : onNegativeClickListener {
                                override fun onNegativeClick() {
                                    mDialog.dismiss()
                                }
                            })
                            mDialog.show()

                        }
                        else
                        {

                            if (can_apply.equals("1")) {
                                bottomsheetDialog()
                            } else {
                                val mDialog = CustomDialog(this@JobsDetailsActivity)
                                mDialog.setTitle("Daily limit reached!")
                                mDialog.setMessage("You have reached to your daily limit to apply jobs. Try tomorrow.")
                                mDialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {
                                    mDialog.dismiss()
                                })
                                mDialog.show()
                            }
                        }
                    } else
                    {*/

                        if (profile_complete!!.toInt() < 60) {
                            val mDialog = ProfileCompletenessAlertDialog(this@JobsDetailsActivity)
                            mDialog.setprogressValue(profile_complete!!.toInt())
                            mDialog.setMessage("Your profile should be completed at least 60% to apply on any job. Please complete your profile first to enjoy the benefits.")
                            mDialog.setPositiveButton("Complete Now", object : onPositiveClickListener {
                                override fun onPositiveClick() {
                                    val bundle = Bundle()
                                    bundle.putString("from", "jobsDetails")
                                    bundle.putString("type", "profileComplete")
                                    bundle.putString("user_id", sharedPreferences.getString(Constants.User_id))
                                    val intent = Intent(this@JobsDetailsActivity, EditProfileActivity::class.java)
                                    intent.putExtras(bundle)
                                    startActivityForResult(intent, 100)
                                    mDialog.dismiss()
                                }
                            })

                            mDialog.setNegativeButton("I'll complete it later", object : onNegativeClickListener {
                                override fun onNegativeClick() {
                                    mDialog.dismiss()
                                }
                            })


                            mDialog.show()
                        }

                        else
                        {

                            if(sharedPreferences.getString(Constants.Membership_id).toString().equals("0"))
                            {
                                val dialog = CustomDialog(this@JobsDetailsActivity)
                                dialog.setTitle("Alert!")
                                dialog.setMessage("This feature is only available to premium or elite members, please purchase a membership.")
                                dialog.setPositiveButton("Buy now", DialogListenerInterface.onPositiveClickListener {
                                    var bundle = Bundle()
                                    bundle.putString("membership_type" , "upgrade")

                                    startActivityForResult(Intent(this@JobsDetailsActivity  , MyMembershipActivity::class.java).putExtras(bundle) ,100)
                                })

                                dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                                    dialog.dismiss()
                                })
                                dialog.show()
                            }
                            else
                            {
                                if (can_apply.equals("1")) {
                                    bottomsheetDialog()
                                } else {
                                    if (/*sharedPreferences.getString(Constants.Membership_id).equals("0")
                                            ||*/
                                            sharedPreferences.getString(Constants.Membership_id).equals("1")
                                            || sharedPreferences.getString(Constants.Membership_id).equals("2"))
                                    {
                                        val dialog = CustomDialog(this@JobsDetailsActivity)
                                        dialog.setTitle("Alert!")
                                        dialog.setMessage("Your daily limit (" + job_apply_count + " jobs) is over. Please upgrade your membership.")
                                        dialog.setPositiveButton("Upgrade now", DialogListenerInterface.onPositiveClickListener {

                                            dialog.dismiss()
                                            var bundle = Bundle()
                                            bundle.putString("membership_type", "upgrade")

                                            startActivityForResult(Intent(this@JobsDetailsActivity, MyMembershipActivity::class.java).putExtras(bundle), 100)
                                        })

                                        dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                                            dialog.dismiss()
                                        })
                                        dialog.show()
                                    } else {
                                        val dialog = CustomDialog(this@JobsDetailsActivity)
                                        dialog.setTitle("Alert!")
                                        dialog.setMessage("Your daily limit (" + job_apply_count + " jobs) is over. Please try again tomorrow to apply more jobs.")
                                        dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

                                            dialog.dismiss()
                                        })

                                        dialog.show()
                                    }


/*                                val mDialog = CustomDialog(this@JobsDetailsActivity)
                                mDialog.setTitle("Become featured now!")
                                mDialog.setMessage("You have reached to your daily limit to apply jobs. Become featured to apply more jobs and stand out from crowd.")
                                mDialog.setPositiveButton("Check Benefits", DialogListenerInterface.onPositiveClickListener {

                                    val newIntent = Intent(this@JobsDetailsActivity, BecomeFeaturedActivity::class.java)
                                    startActivity(newIntent)

                                    mDialog.dismiss()
                                })

                                mDialog.setNegativeButton("No, thanks", DialogListenerInterface.onNegetiveClickListener {
                                    mDialog.dismiss()
                                })

                                mDialog.show()*/

                                }
                            }
                        }
                    //}


                } else {
                    val dialog = CustomDialog(this@JobsDetailsActivity)
                    dialog.setMessage("Your email and mobile number is not verified. Please verify your details to proceed.")
                    dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {

                        val bundle = Bundle()
                        bundle.putString("type", "emailOrPhoneVerification")
                        val newIntent = Intent(this, AccountVerification::class.java)
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
                val dialog = CustomDialog(this@JobsDetailsActivity)
                dialog.setTitle(this@JobsDetailsActivity?.resources?.getString(R.string.published_error_title))
                dialog.setMessage(this@JobsDetailsActivity?.resources?.getString(R.string.published_error_message))
                dialog.setPositiveButton("SETTINGS", DialogListenerInterface.onPositiveClickListener {

                    val newIntent = Intent(this@JobsDetailsActivity, SettingsActivity::class.java)
                    startActivity(newIntent)
                })

                dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                    dialog.dismiss()
                })

                dialog.show()
            }
        }
    }

    fun hireOrRejectDialog(creply_id:String , request_sent: String  , type: String , position:Int)
    {
        val dialog = Dialog(this, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.hire_or_reject_layout)

        val typeTxt = dialog.findViewById<TextView>(R.id.typeTxt)
        val submitBtn = dialog.findViewById<Button>(R.id.messageSubmitBtn)
        val closeButton = dialog.findViewById<ImageView>(R.id.closeButton)
        val messageEdittext = dialog.findViewById<EditText>(R.id.messageEdittext)
        val messageCharactersTxt = dialog.findViewById<TextView>(R.id.messageCharactersTxt)

        if(type.equals("hire", ignoreCase = true))
            typeTxt.text = "Hire User"

        else
            typeTxt.text = "Reject User"

        submitBtn.setOnClickListener {

            if(messageEdittext.text.toString().trim().length!=0)
            mPresenter.hireOrReject(sharedPreferences.getString(Constants.User_id).toString() , creply_id , request_sent  , messageEdittext.text.toString().trim() , position)

            dialog.dismiss()
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        messageEdittext?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int)
            {
                try
                {
                    messageCharactersTxt?.text = "Characters left: " + (300 - s.length)
                }
                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        })

        dialog.show()

    }

    fun bottomsheetDialog()
    {
        mDialog  = Dialog(this, android.R.style.Theme_Light);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setContentView(R.layout.send_message_layout)
        mDialog.show()

        val messageSubmitBtn = mDialog.findViewById<Button>(R.id.messageSubmitBtn)
        val messageCancelBtn = mDialog.findViewById<Button>(R.id.messageCancelBtn)
        val messageEdittext = mDialog.findViewById<EditText>(R.id.messageEdittext)
        val messageCharactersTxt = mDialog.findViewById<TextView>(R.id.messageCharactersTxt)

        messageCancelBtn?.setOnClickListener {
            mDialog.dismiss()
        }

        messageSubmitBtn?.setOnClickListener {
            mPresenter.isMessageValid(messageEdittext?.text.toString().trim())
        }

        messageEdittext?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int)
            {
                try
                {
                    messageCharactersTxt?.text = "Characters left: " + (300 - s.length)
                }
                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun onDestroy()
    {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }

    override fun onGetContact()
    {
        getContactDetailsDialog()
    }

    fun getContactDetailsDialog()
    {
        val dialog = Dialog(this, R.style.NewDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.contact_dialog_layout)

        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)

        dialog.setCanceledOnTouchOutside(true)

        val phoneLayout = dialog.findViewById<LinearLayout>(R.id.phoneLayout)
        val phoneNumber = dialog.findViewById<TextView>(R.id.contactPhone)
        val Email = dialog.findViewById<TextView>(R.id.contactEmail)
        val emailLayout = dialog.findViewById<LinearLayout>(R.id.emailLayout)

        phoneNumber.text = phoneNumberString
        Email.text = emailString

        phoneLayout.setOnClickListener {

            if(!phoneNumberString.equals("Not available" , ignoreCase = true) && !phoneNumberString.equals("null" , ignoreCase = true)&& !phoneNumberString.equals("" , ignoreCase = true))
            {
                try
                {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumberString, null))
                    startActivity(intent)
                }

                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        }

        emailLayout.setOnClickListener {


            if(!emailString.equals("Not available" , ignoreCase = true)
                    && !emailString.equals("null" , ignoreCase = true)
                    && !emailString.equals("" , ignoreCase = true))
            {

                try {

                    val emailIntent = Intent(Intent.ACTION_SENDTO)
                    emailIntent.data = Uri.parse("mailto:" + emailString)
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        dialog.show()
    }
    override fun onSendMessageSuccess(message:String)
    {
        bindingObj.sendMessageBtn.text = "Applied"
        bindingObj.sendMessageBtn2.text = "Applied"
        bindingObj.sendMessageBtn.isEnabled = false
        bindingObj.sendMessageBtn2.isEnabled = false
        bindingObj.sendMessageBtn.isClickable = false
        bindingObj.sendMessageBtn2.isClickable = false

        mDialog.dismiss()
        showSnackbar("Message has been sent successfully.")

        bindingObj.replyLayout.visibility = View.VISIBLE
        bindingObj.currentuserMessage.text = message
        bindingObj.currentuserName.text  = sharedPreferences.getString(Constants.User_name)
        val imageUrl = sharedPreferences.getString(Constants.User_pic)
        Glide.with(this@JobsDetailsActivity)
                .load("https://www.dazzlerr.com/API/"+imageUrl).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(bindingObj.userImage)

            bindingObj.castingReplyPersonName.visibility = View.GONE
            bindingObj.castingReply.visibility = View.GONE
    }


    override fun isMesssageValid(message: String)
    {
        //Calling send message api
        mPresenter.sendMessage(intent.extras!!.getString("call_id","")
        ,sharedPreferences.getString(Constants.User_id).toString(),message
        ,bindingObj.jobdetailsBinder?.email.toString()
        ,bindingObj.jobdetailsBinder?.name.toString()
        ,bindingObj.jobdetailsBinder?.title.toString()
        ,sharedPreferences.getString(Constants.User_name).toString()
        ,bindingObj.jobdetailsBinder?.phone.toString())
    }

    fun isUserLogined():Boolean
    {
        if(sharedPreferences.getString(Constants.User_id).equals(""))
        {
            val dialog = CustomDialog(this@JobsDetailsActivity)
            dialog.setMessage(resources.getString(R.string.signin_txt))
            dialog.setPositiveButton("Continue", DialogListenerInterface.onPositiveClickListener {

                        val bundle = Bundle()
                        bundle.putString("ShouldOpenLogin"  , "true")
                        val newIntent = Intent(this@JobsDetailsActivity, MainActivity::class.java)
                        newIntent.putExtras(bundle)
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(newIntent)})

                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()

            return false
        }

        else
            return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode)
        {

            100->  apiCalling()

        }

    }

}
