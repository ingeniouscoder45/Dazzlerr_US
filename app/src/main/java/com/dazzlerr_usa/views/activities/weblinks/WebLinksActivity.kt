package com.dazzlerr_usa.views.activities.weblinks

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityWebLinksBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.google.android.gms.ads.AdRequest
import javax.inject.Inject

class WebLinksActivity : AppCompatActivity(), View.OnClickListener {


    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj: ActivityWebLinksBinding
    var url : String = ""
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_web_links)
        initializations()
        clicklisterners()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)

        if (intent.extras != null && intent.extras!!.containsKey("type"))
        {
            if(intent.extras!!.getString("type", "").equals("Privacy" ,ignoreCase = true))
            {
                showAd()
                url =  "https://www.dazzlerr.com/app-pages/privacy-policy.php"
                bindingObj.titleLayout.titletxt.text = "Privacy Policy"
            }

            if(intent.extras!!.getString("type", "").equals("How it works" ,ignoreCase = true))
            {
                showAd()
                url =  "https://www.dazzlerr.com/app-pages/how-it-works.php"
                bindingObj.titleLayout.titletxt.text = "How It Works"
            }

            else if(intent.extras!!.getString("type", "").equals("Terms" ,ignoreCase = true))
            {
                showAd()
                url =  "https://www.dazzlerr.com/app-pages/terms-and-condition.php"
                bindingObj.titleLayout.titletxt.text = "Terms & Conditions"
            }

            else if(intent.extras!!.getString("type", "").equals("FAQs" ,ignoreCase = true))
            {
                showAd()
                url =  "https://www.dazzlerr.com/app-pages/faq.php"
                bindingObj.titleLayout.titletxt.text = "FAQ's"
            }

            else if(intent.extras!!.getString("type", "").equals("Partners" ,ignoreCase = true))
            {
                showAd()
                url =  "https://www.dazzlerr.com/app-pages/our-partners.php"
                bindingObj.titleLayout.titletxt.text = "Our Partners"
            }

            else if(intent.extras!!.getString("type", "").equals("explore" ,ignoreCase = true))
            {
                url =  "https://www.dazzlerr.com/app-pages/explore.php"
                bindingObj.titleLayout.titletxt.text = "Explore"
            }

            else if(intent.extras!!.getString("type", "").equals("About" ,ignoreCase = true))
            {
                showAd()
                url =  "https://www.dazzlerr.com/app-pages/about.php"
                bindingObj.titleLayout.titletxt.text = "About"
            }

            else if(intent.extras!!.getString("type", "").equals("facebook" ,ignoreCase = true))
            {
                url =  "https://www.facebook.com/DazzlerrOfficial"
                bindingObj.titleLayout.titletxt.text = ""
            }

            else if(intent.extras!!.getString("type", "").equals("instagram" ,ignoreCase = true))
            {
                url =  "https://www.instagram.com/DazzlerrOfficial/"
                bindingObj.titleLayout.titletxt.text = ""
            }
            else if(intent.extras!!.getString("type", "").equals("twitter" ,ignoreCase = true))
            {
                url =  "https://twitter.com/DazzlerrHQ"
                bindingObj.titleLayout.titletxt.text = ""
            }

            else if(intent.extras!!.getString("type", "").equals("shutdown" ,ignoreCase = true))
            {
                url =  intent.extras?.getString("url")!!
                bindingObj.titleLayout.titletxt.text = ""
            }
        }

        bindingObj.webView.setWebViewClient(MyBrowser())
        bindingObj.webView.getSettings().setLoadsImagesAutomatically(true)
        bindingObj.webView.getSettings().setJavaScriptEnabled(true)
        bindingObj.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        bindingObj.webView.loadUrl(url)
        startProgressBarAnim()
    }

    fun clicklisterners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }


    fun showAd()
    {
        //Initializing Google ads
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
            //--------------------------
        }
    }

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.VISIBLE
    }

    fun stopProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.GONE
    }

    private inner class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            stopProgressBarAnim()


        }
    }

    override fun onClick(v: View?)
    {
        when(v?.id)
        {
            R.id.leftbtn-> finish()
        }
    }
}
