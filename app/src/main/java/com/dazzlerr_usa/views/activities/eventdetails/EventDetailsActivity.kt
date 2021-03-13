package com.dazzlerr_usa.views.activities.eventdetails

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEventDetailsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.eventcontact.EventContactActivity
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class EventDetailsActivity : AppCompatActivity() , EventDetailsView , View.OnClickListener
{
    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var mPresenter: EventDetailsPresenter
    lateinit var bindingObj: ActivityEventDetailsBinding

    var event_slug_name = ""
    var event_name=  ""
    var event_id = ""
    var likeStatus = 0
    lateinit var contactNumberAdapter:ContactNumberAdapter
    var mContactList : MutableList<String> = ArrayList()
    var _EventUrl = ""
    var isEventExpired = false


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        event_id = intent?.extras!!.getString("event_id","")

        apiCalling(sharedPreferences.getString(Constants.User_id).toString() , event_id)
    }
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj =  DataBindingUtil.setContentView(this , R.layout.activity_event_details)
        initializations()
        clickListeners()
        apiCalling(sharedPreferences.getString(Constants.User_id).toString() , event_id)
    }

    fun initializations()
    {

        //----------------Injecting dagger into sharedepreferences
        (application as MyApplication).myComponent.inject(this)

        showAds()
        event_id = intent.extras!!.getString("event_id","")

        Timber.e("event_id : " + event_id)
        mPresenter = EventDetailsModel(this , this)
        bindingObj.titleLayout.titletxt.text = "Event Details"
        bindingObj.titleLayout.rightbtn.setImageResource(R.drawable.ic_share)
        bindingObj.titleLayout.rightbtn.visibility = View.VISIBLE
        bindingObj.titleLayout.rightbtn.setPadding(5, 5, 5 ,5)
        bindingObj.eventDetailsMainLayout.visibility= View.GONE


    }

    fun showAds()
    {

        //Initializing Google ads
        if(sharedPreferences.getString(Constants.Membership_id).equals("")
                ||sharedPreferences.getString(Constants.Membership_id).equals("0")
                ||sharedPreferences.getString(Constants.Membership_id).equals("1")
                ||sharedPreferences.getString(Constants.Membership_id).equals("6")
        ) {

            //MobileAds.initialize(this) {}
            val adRequest = AdRequest.Builder().build()
            bindingObj.adView.visibility = View.VISIBLE
            bindingObj.adView.loadAd(adRequest)
            //-----------------------
        }


    }
    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightbtn.setOnClickListener(this)
        bindingObj.contactUsBtn.setOnClickListener(this)
        bindingObj.likeBtn.setOnClickListener(this)
        bindingObj.emailBtn.setOnClickListener(this)
        bindingObj.callBtn.setOnClickListener(this)
        bindingObj.websiteBtn.setOnClickListener(this)
    }

    private fun apiCalling(user_id:String ,profile_id:String)
    {

        if(this@EventDetailsActivity?.isNetworkActive()!!)
        {
            mPresenter?.getEventDetails(user_id ,event_id)
        }

        else
        {
            val dialog = CustomDialog(this@EventDetailsActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(user_id ,profile_id)
                    })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }

    override fun onSuccess(model: EventDetailsPojo)
    {
        if (model.result?.size != 0)
        {

            bindingObj.binderObj = model.result?.get(0)
            if(model.result?.get(0)?._post_views.isNullOrEmpty() )
            bindingObj.binderObj?._post_views= "0"

            else
                bindingObj.binderObj?._post_views=model.result?.get(0)?._post_views

            if(model.result?.get(0)?.price!!.isNotEmpty() )
            bindingObj.binderObj?.price= if(Constants.CURRENT_CURRENCY.isEmpty() || Constants.CURRENT_CURRENCY.equals("inr",ignoreCase = true)) Constants.RupeesSign else Constants.DollorSign + model.result?.get(0)?.price


            bindingObj.executePendingBindings()
            bindingObj.eventDetailsMainLayout.visibility= View.VISIBLE

            if(!model.result?.get(0)?.like_status!!.equals("") && !model.result?.get(0)?.like_status.equals("null"))
                likeStatus  = model.result?.get(0)?.like_status!!.toInt()

            else
                likeStatus = 0

            likeDislikeHandler()

            if(model.result?.get(0)?.contact_phone!=null && !model.result?.get(0)?.contact_phone.equals(""))
                mContactList  = model.result?.get(0)?.contact_phone?.split((Regex("\\n|,"))) as MutableList<String>


            contactNumberAdapter = ContactNumberAdapter(this ,mContactList )
            bindingObj.contactNumberRecycler.layoutManager = LinearLayoutManager(this@EventDetailsActivity , LinearLayoutManager.HORIZONTAL ,false)
            bindingObj.contactNumberRecycler.adapter = contactNumberAdapter

            event_name = model.result?.get(0)?.event_title!!
            event_slug_name = model.result?.get(0)?.event_slug!!
            _EventUrl = model.result?.get(0)?.event_url!!
            //event_id = model.result?.get(0)?.id.toString()

           // likeStatus  = model.result?.get(0)?.like_status as Int

            fullDateFormat(bindingObj.eventDate ,model.result?.get(0)?._EventStartDate!! )
            fullTimeFormat(bindingObj.eventTime ,model.result?.get(0)?._EventStartDate!! )
            isEventExpired(model.result?.get(0)?._EventStartDate!!)//Showing or hiding expired event badge
            likeDislikeHandler()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bindingObj.eventDetails.setText(Html.fromHtml(model.result?.get(0)?.description, Html.FROM_HTML_MODE_LEGACY));
            }

            else
            {
                bindingObj.eventDetails.setText(Html.fromHtml(model.result?.get(0)?.description));
            }

            Glide.with(this@EventDetailsActivity)
                    .load( model.result?.get(0)?.event_image.toString())
                    .apply(RequestOptions().centerCrop())
                    .thumbnail(0.3f)
                    .placeholder(R.drawable.event_placeholder)
                    .into(bindingObj.eventImage)

        }
    }

    fun fullTimeFormat(view: TextView, date : String)
    {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val date = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
            val dateStr = formatter.format(date)
            view.text = dateStr
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            view.text = date
        }
    }

    fun isEventExpired(date : String)
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val date = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed

            if(date.before(Date()))
            {
                isEventExpired = true
                bindingObj.eventExpiredTxt.visibility = View.VISIBLE
            }
        }

        catch (e:Exception)
        {
            e.printStackTrace()
            bindingObj.eventExpiredTxt.visibility = View.GONE
            isEventExpired = false
        }
    }

    override fun onLikeOrDislike(status: String)
    {
        likeStatus = status.toInt()
        likeDislikeHandler()
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
        try {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    fun likeDislikeHandler()
    {
        if(likeStatus==1)
        {
            bindingObj.likeBtnTxt.text = "Liked"
            bindingObj.likeBtnImg.setImageResource(R.drawable.heart_selected)
            bindingObj.likeBtnImg.setColorFilter(ContextCompat.getColor(this@EventDetailsActivity, R.color.appColor), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
        else
        {
            bindingObj.likeBtnTxt.text = "Like"
            bindingObj.likeBtnImg.setImageResource(R.drawable.like_outline)
            bindingObj.likeBtnImg.setColorFilter(ContextCompat.getColor(this@EventDetailsActivity, R.color.colorLightGrey), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn-> finish()

            R.id.rightbtn->
            {
                if(isUserLogined() && !_EventUrl.equals(""))
                {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, _EventUrl)

                        // (Optional) Here we're setting the title of the content
                        putExtra(Intent.EXTRA_TITLE, "Dazzlerr")
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Dazzlerr - Connecting Talent");
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }

            R.id.websiteBtn->
            {

                var url = bindingObj.binderObj?.contact_website.toString()

                if(!url.equals("") && !url.equals("null"))
                {
                    try
                    {
                        if (!url.startsWith("https://") && !url.startsWith("http://"))
                        {
                            url = "http://"+url
                        }
                        val openUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(openUrlIntent)

                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                }

            }

            R.id.emailBtn->
            {
                if(!bindingObj.binderObj?.contact_email!!.equals("") && !bindingObj.binderObj?.contact_email!!.equals("null")) {
                    try {
                        val emailIntent = Intent(Intent.ACTION_SENDTO)
                        emailIntent.data = Uri.parse("mailto:"+bindingObj.binderObj?.contact_email!!)
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }


/*            R.id.callBtn->
            {

                if(!bindingObj.binderObj?.contact_phone!!.equals("") && !bindingObj?.binderObj?.contact_phone!!.equals("null")) {

                    try {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", bindingObj?.binderObj?.contact_phone!!, null))
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }*/

            R.id.contactUsBtn->
            {

                if(isEventExpired)
                {
                    showSnackbar("Sorry! This event has been expired.")
                }
                else {
                    if (event_name.length != 0) {
                        val bundle = Bundle()
                        bundle.putString("profile_name", event_name)
                        bundle.putString("username", event_slug_name)
                        bundle.putString("event_id", event_id)
                        startActivity(Intent(this@EventDetailsActivity, EventContactActivity::class.java).putExtras(bundle))
                    }
                }
            }

            R.id.likeBtn->
            {
                if(isUserLogined())
                {
                    if(likeStatus==1)
                        mPresenter.likeOrDislike(sharedPreferences.getString(Constants.User_id).toString() ,event_id , ""+0)

                    else if(likeStatus==0)
                        mPresenter.likeOrDislike(sharedPreferences.getString(Constants.User_id).toString() ,event_id , ""+1)

                }
            }
        }
    }

    fun isUserLogined():Boolean
    {
        if(sharedPreferences.getString(Constants.User_id).equals(""))
        {
            val dialog  =  CustomDialog(this)
            dialog.setMessage(resources.getString(R.string.signin_txt))
            dialog.setPositiveButton("Continue", DialogListenerInterface.onPositiveClickListener {

                        val bundle = Bundle()
                        bundle.putString("ShouldOpenLogin"  , "true")
                        val newIntent = Intent(this@EventDetailsActivity, MainActivity::class.java)
                        newIntent.putExtras(bundle)
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(newIntent)
                    })
                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()

            return false
        }

        else
            return true
    }
}
