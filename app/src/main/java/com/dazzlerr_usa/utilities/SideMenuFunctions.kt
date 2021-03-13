package com.dazzlerr_usa.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.BuildConfig

import com.dazzlerr_usa.R
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.blogs.activities.BlogListActivity
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.activities.settings.SettingsActivity
import com.dazzlerr_usa.views.activities.contactus.ContactUsActivity
import com.dazzlerr_usa.views.activities.elitemembers.EliteMembersActivity
import com.dazzlerr_usa.views.activities.events.activities.EventListActivity
import com.dazzlerr_usa.views.activities.influencers.InfluencersActivity
import com.dazzlerr_usa.views.activities.institute.InstituteListActivity
import com.dazzlerr_usa.views.activities.messages.MessagesActivity
import com.dazzlerr_usa.views.activities.myJobs.MyJobsActivity
import com.dazzlerr_usa.views.activities.mymembership.MyMembershipActivity
import com.dazzlerr_usa.views.activities.notifications.NotificationsActivity
import com.dazzlerr_usa.views.activities.shortlistedtalents.ShortListedTalentsActivity
import com.dazzlerr_usa.views.activities.transactions.TransactionsActivity
import com.dazzlerr_usa.views.fragments.castingprofile.localfragments.CastingProfileFragment
import com.dazzlerr_usa.views.fragments.home.HomeFragment
import com.dazzlerr_usa.views.fragments.profile.FeaturedProfileFragment
import com.dazzlerr_usa.views.fragments.profile.ProfileFragment
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import com.dazzlerr_usa.views.activities.weblinks.WebLinksActivity

class SideMenuFunctions(internal var menu: SlidingMenu, internal var con: Context) : OnClickListener
{

    internal var nav_profile_pic: ImageView
    internal var navCrossBtn: ImageView
    internal var fm: androidx.fragment.app.FragmentManager
    internal var nav_AboutBtn: LinearLayout
    internal var nav_dashboardBtn: LinearLayout
    internal var nav_MyMessagesBtn: LinearLayout
    internal var nav_NotificationBtn: LinearLayout
    internal var nav_SettingsBtn: LinearLayout
    internal var nav_LogoutBtn: LinearLayout
    internal var nav_LoginBtn: LinearLayout
    internal var nav_PrivacyBtn: LinearLayout
    internal var nav_FAQBtn: LinearLayout
    internal var nav_TermsBtn: LinearLayout
    internal var nav_ContactUsBtn: LinearLayout
    internal var nav_timelineLayout: LinearLayout
    internal var nav_welcomeLayout: LinearLayout
    internal var nav_EliteMembersBtn: LinearLayout
    internal var ratePlaystoreBtn: LinearLayout
    internal var nav_ShortlistedBtn: LinearLayout
    internal var nav_PartnersBtn: LinearLayout
    internal var nav_HowItWorksBtn: LinearLayout
    internal var nav_BlogsBtn: LinearLayout
    internal var nav_TransactionBtn: LinearLayout
    internal var nav_ExploreBtn: LinearLayout
    internal var nav_MembershipBtn: LinearLayout

    internal var nav_InfluencersBtn: LinearLayout
    internal var nav_InstitutesBtn: LinearLayout

    internal var nav_EventsBtn: LinearLayout
    internal var nav_MyJobsBtn: LinearLayout
    internal var nav_InstagramBtn: ImageView
    internal var nav_FacebookBtn: ImageView
    internal var nav_WhatsappBtn: ImageView

    internal var navDashboard_txt: TextView
    internal var nav_name: TextView
    internal var nav_profession: TextView
    internal var nav_location: TextView
    internal var nav_accountType: TextView
    internal var nav_AppversionTxt: TextView
    internal var navMyMessagesCount_txt: TextView
    internal var navTopLoginBtn: TextView
    internal var navTopRegisterBtn: TextView

    init
    {

        fm = (con as AppCompatActivity).supportFragmentManager

        nav_profile_pic = menu.findViewById<View>(R.id.nav_image) as ImageView
        navCrossBtn = menu.findViewById<View>(R.id.navCrossBtn) as ImageView
        nav_NotificationBtn = menu.findViewById<View>(R.id.nav_NotificationBtn) as LinearLayout
        nav_dashboardBtn = menu.findViewById<View>(R.id.nav_dashboardBtn) as LinearLayout
        nav_MyMessagesBtn = menu.findViewById<View>(R.id.nav_MyMessagesBtn) as LinearLayout
        nav_AboutBtn = menu.findViewById<View>(R.id.nav_AboutBtn) as LinearLayout
        nav_SettingsBtn = menu.findViewById<View>(R.id.nav_SettingsBtn) as LinearLayout
        nav_LogoutBtn = menu.findViewById<View>(R.id.nav_LogoutBtn) as LinearLayout
        nav_LoginBtn = menu.findViewById<View>(R.id.nav_LoginBtn) as LinearLayout
        nav_PrivacyBtn = menu.findViewById<View>(R.id.nav_PrivacyBtn) as LinearLayout
        nav_FAQBtn = menu.findViewById<View>(R.id.nav_FAQBtn) as LinearLayout
        nav_TermsBtn = menu.findViewById<View>(R.id.nav_TermsBtn) as LinearLayout
        nav_ContactUsBtn = menu.findViewById<View>(R.id.nav_ContactUsBtn) as LinearLayout
        nav_timelineLayout = menu.findViewById<View>(R.id.nav_timelineLayout) as LinearLayout
        nav_welcomeLayout = menu.findViewById<View>(R.id.nav_welcomeLayout) as LinearLayout
        ratePlaystoreBtn = menu.findViewById<View>(R.id.ratePlaystoreBtn) as LinearLayout
        nav_HowItWorksBtn = menu.findViewById<View>(R.id.nav_HowItWorksBtn) as LinearLayout
        nav_PartnersBtn = menu.findViewById<View>(R.id.nav_PartnersBtn) as LinearLayout
        nav_BlogsBtn = menu.findViewById<View>(R.id.nav_BlogsBtn) as LinearLayout
        nav_TransactionBtn = menu.findViewById<View>(R.id.nav_TransactionBtn) as LinearLayout
        nav_ExploreBtn = menu.findViewById<View>(R.id.nav_ExploreBtn) as LinearLayout
        nav_MembershipBtn = menu.findViewById<View>(R.id.nav_MembershipBtn) as LinearLayout
        nav_InstitutesBtn = menu.findViewById<View>(R.id.nav_InstituteBtn) as LinearLayout

        nav_InstagramBtn = menu.findViewById<View>(R.id.nav_InstagramBtn) as ImageView
        nav_FacebookBtn = menu.findViewById<View>(R.id.nav_FacebookBtn) as ImageView
        nav_WhatsappBtn = menu.findViewById<View>(R.id.nav_WhatsappBtn) as ImageView
        navDashboard_txt = menu.findViewById<View>(R.id.navDashboard_txt) as TextView
        nav_name = menu.findViewById<View>(R.id.nav_name) as TextView
        nav_profession = menu.findViewById<View>(R.id.nav_profession) as TextView
        nav_AppversionTxt = menu.findViewById<View>(R.id.nav_AppversionTxt) as TextView
        nav_location = menu.findViewById<View>(R.id.nav_location) as TextView
        nav_accountType = menu.findViewById<View>(R.id.nav_accountType) as TextView
        navMyMessagesCount_txt = menu.findViewById<View>(R.id.navMyMessagesCount_txt) as TextView
        nav_EliteMembersBtn = menu.findViewById<View>(R.id.nav_EliteMembersBtn) as LinearLayout
        nav_InfluencersBtn = menu.findViewById<View>(R.id.nav_InfluencersBtn) as LinearLayout
        nav_EventsBtn = menu.findViewById<View>(R.id.nav_EventsBtn) as LinearLayout
        nav_MyJobsBtn = menu.findViewById<View>(R.id.nav_MyJobsBtn) as LinearLayout
        nav_ShortlistedBtn = menu.findViewById<View>(R.id.nav_ShortlistedBtn) as LinearLayout


        navTopLoginBtn = menu.findViewById<View>(R.id.navTopLoginBtn) as TextView
        navTopRegisterBtn = menu.findViewById<View>(R.id.navTopRegisterBtn) as TextView

        nav_NotificationBtn.setOnClickListener(this)
        nav_dashboardBtn.setOnClickListener(this)
        nav_MyMessagesBtn.setOnClickListener(this)
        nav_AboutBtn.setOnClickListener(this)
        nav_SettingsBtn.setOnClickListener(this)
        nav_LogoutBtn.setOnClickListener(this)
        nav_LoginBtn.setOnClickListener(this)
        nav_PrivacyBtn.setOnClickListener(this)
        nav_FAQBtn.setOnClickListener(this)
        nav_TermsBtn.setOnClickListener(this)
        nav_ContactUsBtn.setOnClickListener(this)
        nav_profile_pic.setOnClickListener(this)
        navCrossBtn.setOnClickListener(this)
        nav_EliteMembersBtn.setOnClickListener(this)
        nav_InfluencersBtn.setOnClickListener(this)
        nav_EventsBtn.setOnClickListener(this)
        nav_MyJobsBtn.setOnClickListener(this)
        nav_ShortlistedBtn.setOnClickListener(this)
        ratePlaystoreBtn.setOnClickListener(this)
        nav_PartnersBtn.setOnClickListener(this)
        nav_HowItWorksBtn.setOnClickListener(this)
        nav_BlogsBtn.setOnClickListener(this)
        nav_TransactionBtn.setOnClickListener(this)
        nav_ExploreBtn.setOnClickListener(this)
        nav_MembershipBtn.setOnClickListener(this)
        nav_InstitutesBtn.setOnClickListener(this)

        nav_InstagramBtn.setOnClickListener(this)
        nav_FacebookBtn.setOnClickListener(this)
        nav_WhatsappBtn.setOnClickListener(this)
        nav_timelineLayout.setOnClickListener(this)
        nav_profile_pic.setOnClickListener(this)
        navTopRegisterBtn.setOnClickListener(this)
        navTopLoginBtn.setOnClickListener(this)

        navSettings()

    }

    fun navSettings()
    {

        nav_AppversionTxt.text = "Dazzlerr Version "+ BuildConfig.VERSION_NAME
        if((con as HomeActivity).sharedPreferences.getString(Constants.User_type)
                        .equals("GuestUser", ignoreCase = true))
        {

            nav_timelineLayout.visibility = View.GONE
            nav_SettingsBtn.visibility =View.GONE
            //nav_NotificationBtn.visibility =View.GONE
            nav_LogoutBtn.visibility =View.GONE
            nav_MyMessagesBtn.visibility =View.GONE
            nav_LoginBtn.visibility =View.VISIBLE
            nav_welcomeLayout.visibility =View.VISIBLE
            nav_SettingsBtn.visibility = View.GONE
            nav_TransactionBtn.visibility = View.GONE
            nav_MyJobsBtn.visibility = View.GONE
            nav_ShortlistedBtn.visibility = View.GONE
            nav_MembershipBtn.visibility = View.GONE
        }

        else
        {

            if((con as HomeActivity).sharedPreferences.getString(Constants.User_Role)
                            .equals("casting", ignoreCase = true))
            {
                nav_MyJobsBtn.visibility = View.VISIBLE
                nav_ShortlistedBtn.visibility = View.VISIBLE
                nav_TransactionBtn.visibility = View.GONE
                nav_MembershipBtn.visibility = View.GONE
            }

            else
            {
                nav_MyJobsBtn.visibility = View.GONE
                nav_ShortlistedBtn.visibility = View.GONE
                nav_TransactionBtn.visibility = View.VISIBLE
                nav_MembershipBtn.visibility = View.VISIBLE
            }

            setProfilePic()
        }

    }

    fun setProfilePic()
    {

        if(Constants.User_Messages_Count.equals("") || Constants.User_Messages_Count.equals("0")  || Constants.User_Messages_Count.equals("null") )
            navMyMessagesCount_txt.visibility = View.GONE

        else {
            navMyMessagesCount_txt.visibility = View.VISIBLE
            navMyMessagesCount_txt.text = Constants.User_Messages_Count
        }

        if((con as HomeActivity).sharedPreferences.getString(Constants.User_Role).equals("casting", ignoreCase = true))
            nav_profession.text = ((con as HomeActivity).sharedPreferences.getString(Constants.CASTING_REPRESENTER).toString().capitalize())

        else
        {
            nav_profession.text =  (con as HomeActivity).sharedPreferences.getString(Constants.User_Role)!!.capitalize() +""+ if((con as HomeActivity).sharedPreferences.getString(Constants.User_Secondary_Role)?.isNotEmpty()!!) " | "+(con as HomeActivity).sharedPreferences.getString(Constants.User_Secondary_Role)?.capitalize() else ""
        }

        nav_location.text  = ""
        nav_name.text = (con as HomeActivity).sharedPreferences.getString(Constants.User_name)

        nav_accountType.text = "Membership Type: "+(con as HomeActivity).sharedPreferences.getString(Constants.Account_type).toString().capitalize()

        if(!(con as HomeActivity).sharedPreferences.getString(Constants.Current_city).equals("") && !(con as HomeActivity).sharedPreferences.getString(Constants.Current_city).equals("null"))
            nav_location.text = (con as HomeActivity).sharedPreferences.getString(Constants.Current_city)

        if(!nav_location.text.toString().trim().equals("") && !nav_location.text.toString().trim().equals((con as HomeActivity).sharedPreferences.getString(Constants.Current_state),ignoreCase = true))
        {
            nav_location.text = nav_location.text.toString().trim()+", "+(con as HomeActivity).sharedPreferences.getString(Constants.Current_state)
        }

        else
        {
            nav_location.text = (con as HomeActivity).sharedPreferences.getString(Constants.Current_state)
        }

        val imageUrl = (con as HomeActivity).sharedPreferences.getString(Constants.User_pic)
        Glide.with(con as HomeActivity)
                .load("https://www.dazzlerr.com/API/"+imageUrl).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(nav_profile_pic)
    }

    override fun onClick(v: View)
    {

        val f = (con as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.fragment_container)
        when (v.id)
        {

            R.id.nav_ContactUsBtn->
            {
                (con as HomeActivity).slidemenuOff()
                val newIntent = Intent(con, ContactUsActivity::class.java)
                con.startActivity(newIntent)
            }

            R.id.nav_MyMessagesBtn->
            {
                (con as HomeActivity).slidemenuOff()

                val newIntent = Intent(con, MessagesActivity::class.java)
                con.startActivity(newIntent)
            }

            R.id.nav_TransactionBtn->
            {
                (con as HomeActivity).slidemenuOff()

                val newIntent = Intent(con, TransactionsActivity::class.java)
                con.startActivity(newIntent)
            }

            R.id.nav_MembershipBtn->
            {
                (con as HomeActivity).slidemenuOff()

                    val newIntent = Intent(con, MyMembershipActivity::class.java)
                    con.startActivity(newIntent)

            }

            R.id.nav_PrivacyBtn->
            {
                (con as HomeActivity).slidemenuOff()

                val bundle = Bundle()
                bundle.putString("type", "Privacy")
                val newIntent = Intent(con, WebLinksActivity::class.java)
                newIntent.putExtras(bundle)
                con.startActivity(newIntent)
            }

            R.id.nav_ExploreBtn->
            {
                (con as HomeActivity).slidemenuOff()

                val bundle = Bundle()
                bundle.putString("type", "explore")
                val newIntent = Intent(con, WebLinksActivity::class.java)
                newIntent.putExtras(bundle)
                con.startActivity(newIntent)
            }

            R.id.nav_PartnersBtn->
            {
                (con as HomeActivity).slidemenuOff()

                val bundle = Bundle()
                bundle.putString("type", "Partners")
                val newIntent = Intent(con, WebLinksActivity::class.java)
                newIntent.putExtras(bundle)
                con.startActivity(newIntent)
            }


            R.id.nav_HowItWorksBtn->
            {
                (con as HomeActivity).slidemenuOff()

                val bundle = Bundle()
                bundle.putString("type", "How it works")
                val newIntent = Intent(con, WebLinksActivity::class.java)
                newIntent.putExtras(bundle)
                con.startActivity(newIntent)
            }


            R.id.nav_AboutBtn->
            {
                (con as HomeActivity).slidemenuOff()

                val bundle = Bundle()
                bundle.putString("type", "About")
                val newIntent = Intent(con, WebLinksActivity::class.java)
                newIntent.putExtras(bundle)
                con.startActivity(newIntent)
            }

            R.id.nav_FAQBtn->
            {
                (con as HomeActivity).slidemenuOff()

                val bundle = Bundle()
                bundle.putString("type", "FAQs")
                val newIntent = Intent(con, WebLinksActivity::class.java)
                newIntent.putExtras(bundle)
                con.startActivity(newIntent)
            }

            R.id.nav_TermsBtn->
            {
                (con as HomeActivity).slidemenuOff()

                val bundle = Bundle()
                bundle.putString("type", "Terms")
                val newIntent = Intent(con, WebLinksActivity::class.java)
                newIntent.putExtras(bundle)
                con.startActivity(newIntent)
            }


            R.id.nav_WhatsappBtn->
            {
                (con as HomeActivity).slidemenuOff()

                (con as HomeActivity).slidemenuOff()

                try {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+91 85568 93167", null))
                    con.startActivity(intent)
                }
                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }


            R.id.nav_FacebookBtn->
            {
                (con as HomeActivity).slidemenuOff()

                try {
                    con.getPackageManager().getPackageInfo("com.facebook.katana", 0)
                    con.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/272981166484275")))
                } catch (e: Exception) {
                    val bundle = Bundle()
                    bundle.putString("type", "facebook")
                    val newIntent = Intent(con, WebLinksActivity::class.java)
                    newIntent.putExtras(bundle)
                    con.startActivity(newIntent)
                }

            }


            R.id.nav_InstagramBtn->
            {
                (con as HomeActivity).slidemenuOff()

                try {
                    val uri = Uri.parse("https://www.instagram.com/DazzlerrOfficial")
                    val likeIng = Intent(Intent.ACTION_VIEW, uri)
                    likeIng.setPackage("com.instagram.android")
                    con.startActivity(likeIng)
                }
                catch (e: Exception) {
                    val bundle = Bundle()
                    bundle.putString("type", "instagram")
                    val newIntent = Intent(con, WebLinksActivity::class.java)
                    newIntent.putExtras(bundle)
                    con.startActivity(newIntent)
                }


            }

            R.id.ratePlaystoreBtn->
            {
                (con as HomeActivity).slidemenuOff()
                val appPackageName = con.packageName // getPackageName() from Context or Activity object
                try {
                    con.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    con.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }


            R.id.nav_dashboardBtn -> {
                (con as HomeActivity).slidemenuOff()

                if (f !is HomeFragment) {
                    (con as HomeActivity).loadFirstFragment(HomeFragment())
                }
            }

            R.id.nav_image ->
            {
                (con as HomeActivity).slidemenuOff()

                val bundle = Bundle()
                bundle.putString("from", "dashboard")
                bundle.putString("user_id", (con as HomeActivity).sharedPreferences.getString(Constants.User_id))
                bundle.putString("user_role", (con as HomeActivity).sharedPreferences.getString(Constants.User_Role))

                if ((con as HomeActivity).sharedPreferences.getString(Constants.User_Role).equals("casting", ignoreCase = true))
                {
                    if(f !is CastingProfileFragment)
                    {
                        val fragment = CastingProfileFragment()
                        fragment.arguments = bundle
                        (con as HomeActivity).loadFirstFragment(fragment)
                    }

                }
                else
                {

                    if((con as HomeActivity).sharedPreferences.getString(Constants.Is_Featured).equals("1"))
                    {
                        if (f !is FeaturedProfileFragment) {
                            val fragment = FeaturedProfileFragment()
                            fragment.arguments = bundle
                            (con as HomeActivity).loadFirstFragment(fragment)
                        }
                    }
                    else {
                        if (f !is ProfileFragment) {
                            val fragment = ProfileFragment()
                            fragment.arguments = bundle
                            (con as HomeActivity).loadFirstFragment(fragment)
                        }

                    }
                }
            }

            R.id.nav_NotificationBtn -> {
                (con as HomeActivity).slidemenuOff()
                con.startActivity(Intent(con , NotificationsActivity::class.java))
            }

            R.id.nav_ShortlistedBtn -> {
                (con as HomeActivity).slidemenuOff()
                con.startActivity(Intent(con , ShortListedTalentsActivity::class.java))
            }

            R.id.nav_MyJobsBtn -> {
                (con as HomeActivity).slidemenuOff()
                con.startActivity(Intent(con , MyJobsActivity::class.java))
            }

            R.id.nav_EliteMembersBtn -> {
                (con as HomeActivity).slidemenuOff()
                con.startActivity(Intent(con , EliteMembersActivity::class.java))
            }

            R.id.nav_InfluencersBtn -> {
                (con as HomeActivity).slidemenuOff()
                con.startActivity(Intent(con , InfluencersActivity::class.java))
            }
            R.id.nav_EventsBtn -> {
                (con as HomeActivity).slidemenuOff()
                con.startActivity(Intent(con , EventListActivity::class.java))
            }

            R.id.nav_InstituteBtn -> {
                (con as HomeActivity).slidemenuOff()
                con.startActivity(Intent(con , InstituteListActivity::class.java))
            }


            R.id.nav_BlogsBtn -> {
                (con as HomeActivity).slidemenuOff()
                con.startActivity(Intent(con , BlogListActivity::class.java))
            }

            R.id.nav_SettingsBtn -> {
                (con as HomeActivity).slidemenuOff()
                (con as HomeActivity).startActivityForResult(Intent(con , SettingsActivity::class.java) ,100)
            }

            R.id.navCrossBtn -> (con as HomeActivity).slidemenuOff()

            R.id.nav_LogoutBtn -> {
               (con as HomeActivity).slidemenuOff()
               logout()
            }

            R.id.nav_LoginBtn -> {
               // (con as DashboardActivity).sharedPreferences.clear()
                val bundle = Bundle()
                bundle.putString("ShouldOpenLogin"  , "true")
                val newIntent = Intent(con, MainActivity::class.java)
                newIntent.putExtras(bundle)
                con.startActivity(newIntent)
            }

            R.id.navTopLoginBtn -> {
                // (con as DashboardActivity).sharedPreferences.clear()
                val bundle = Bundle()
                bundle.putString("ShouldOpenLogin"  , "true")
                val newIntent = Intent(con, MainActivity::class.java)
                newIntent.putExtras(bundle)
                con.startActivity(newIntent)
            }

            R.id.navTopRegisterBtn -> {
                // (con as DashboardActivity).sharedPreferences.clear()
                val bundle = Bundle()
                bundle.putString("ShouldOpenRegister"  , "true")
                val newIntent = Intent(con, MainActivity::class.java)
                newIntent.putExtras(bundle)
                con.startActivity(newIntent)
            }

        }

    }

    fun logout()
    {

        var dialog  = CustomDialog(con);
        dialog.setMessage("Are you sure that you want to logout?")
        dialog.setPositiveButton("Yes" , DialogListenerInterface.onPositiveClickListener {
            dialog.dismiss()
           // (con as HomeActivity).mPresenter.clearDeviceId( (con as HomeActivity).sharedPreferences.getString(Constants.User_id).toString())

            (con as HomeActivity).sharedPreferences.clear()

            val bundle = Bundle()
            bundle.putString("ShouldOpenLogin"  , "true")
            val newIntent = Intent(con, MainActivity::class.java)
            newIntent.putExtras(bundle)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK)
            con.startActivity(newIntent)

        })
        dialog.setNegativeButton("No" , DialogListenerInterface.onNegetiveClickListener {
            dialog.dismiss()
        })
        dialog.show()

    }
}
