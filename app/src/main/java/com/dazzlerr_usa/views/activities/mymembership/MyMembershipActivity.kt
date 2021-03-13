package com.dazzlerr_usa.views.activities.mymembership

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.membership.MembershipActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_my_membership.*
import kotlinx.android.synthetic.main.simpletitlelayout.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class MyMembershipActivity : AppCompatActivity() ,View.OnClickListener, MyMembershipView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var mPresenter: MyMembershipPresenter
    var isRenewAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_membership)
        initializations()
        listeners()
    }


    fun initializations()
    {
        titletxt.text = "Membership"
        mPresenter  = MyMembershipModel(this , this)
        (application as MyApplication).myComponent.inject(this)

        membershipMainLayout.visibility = View.GONE
        upgradeMembershipBtn.visibility = View.GONE
        renewMembershipBtn.visibility = View.GONE

        myMembershipRecycler.layoutManager = LinearLayoutManager(this)
        myMembershipRecycler.addItemDecoration( DividerItemDecoration(myMembershipRecycler.getContext(), DividerItemDecoration.VERTICAL))
    }

    fun listeners()
    {
        upgradeMembershipBtn.setOnClickListener(this)
        renewMembershipBtn.setOnClickListener(this)
        leftbtn.setOnClickListener(this)
    }

    fun apiCalling()
    {
        if(isNetworkActiveWithMessage())
        mPresenter.getMyMembership(sharedPreferences.getString(Constants.User_id).toString())
    }


    override fun onSuccess(model: MyMembershipPojo)
    {

        membershipMainLayout.visibility = View.VISIBLE
        upgradeMembershipBtn.visibility = View.VISIBLE
        renewMembershipBtn.visibility = View.VISIBLE

        membershipItemPlanTxt.text = model.result[0].my_plan.capitalize()
        dateFormatter(model.result[0].membership_end_date , membershipPlanExpiryDateTxt)

        myMembershipRecycler.adapter = MyMembershipPlansAdapter(this@MyMembershipActivity , model.features)

    }

    fun dateFormatter(date : String , textview : TextView)
    {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
        val formatter = SimpleDateFormat("dd MMM yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
        textview.text = formatter.format(datetime)

        val toDateCalendar = Calendar.getInstance()
        toDateCalendar.set(datetime.year, datetime.month, datetime.day)


        val fromDateCalendar = Calendar.getInstance()

        // if expiry date is less than 7 then user can renew
        isRenewAvailable = numDaysBetween(Calendar.getInstance() , fromDateCalendar.timeInMillis ,datetime.time)<=7

    }

    fun numDaysBetween(c: Calendar, fromTime: Long, toTime: Long): Int
    {
        var result = 0
        if (toTime <= fromTime) return result
        c.timeInMillis = toTime
        val toYear = c[Calendar.YEAR]
        result += c[Calendar.DAY_OF_YEAR]
        c.timeInMillis = fromTime
        result -= c[Calendar.DAY_OF_YEAR]
        while (c[Calendar.YEAR] < toYear) {
            result += c.getActualMaximum(Calendar.DAY_OF_YEAR)
            c.add(Calendar.YEAR, 1)
        }
        return result
    }

    override fun showProgress(visiblity: Boolean) {

        if(visiblity)
            startProgressBarAnim()

        else
            stopProgressBarAnim()
    }

    fun stopProgressBarAnim() {

        aviProgressBar.visibility = View.GONE
    }
    fun startProgressBarAnim() {

        aviProgressBar.visibility = View.VISIBLE
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
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
            R.id.upgradeMembershipBtn->
            {
                var bundle = Bundle()
                bundle.putString("membership_type" , "upgrade")
                bundle.putString("isDowngradeEnabled", "false")
                startActivity(Intent(this@MyMembershipActivity  , MembershipActivity::class.java).putExtras(bundle))
            }


            R.id.renewMembershipBtn->
            {

                if(isRenewAvailable) {
                    var bundle = Bundle()
                    bundle.putString("membership_type", "upgrade")
                    bundle.putString("isDowngradeEnabled", "true")
                    startActivity(Intent(this@MyMembershipActivity, MembershipActivity::class.java).putExtras(bundle))

                }

                else
                {
                    val dialog = CustomDialog(this@MyMembershipActivity)
                    dialog.setTitle("Alert!")
                    dialog.setMessage("Your membership will be available for renew only before one week of your expiry date.")
                    dialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
                }

            }

            R.id.leftbtn->
            {
                finish()
            }

        }
    }

    override fun onResume() {
        super.onResume()

        apiCalling()
    }

}
