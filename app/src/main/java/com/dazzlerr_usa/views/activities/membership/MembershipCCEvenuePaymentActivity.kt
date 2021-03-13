package com.dazzlerr_usa.views.activities.membership

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityWebviewBinding
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.ccavenueutility.AvenuesParams
import com.dazzlerr_usa.utilities.ccavenueutility.Constants
import com.dazzlerr_usa.utilities.ccavenueutility.RSAUtility
import com.dazzlerr_usa.utilities.ccavenueutility.ServiceUtility
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.utilities.retrofit.WebServices
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.io.StringReader
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import javax.inject.Inject

class MembershipCCEvenuePaymentActivity : AppCompatActivity() {

    var mainIntent: Intent? = null
    var encVal: String? = ""
    var vResponse: String? = null
    lateinit var bindingObj: ActivityWebviewBinding

    lateinit var apiHelper: APIHelper

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    public override fun onCreate(bundle: Bundle?)
    {

        super.onCreate(bundle)
        bindingObj  = DataBindingUtil.setContentView(this , R.layout.activity_webview)
        (application as MyApplication).myComponent.inject(this@MembershipCCEvenuePaymentActivity)
        mainIntent = intent
        Timber.e("Amount " + mainIntent!!.getStringExtra(AvenuesParams.AMOUNT))
        //get rsa key method
        apiHelper = APIHelper()
        get_RSA_key(ServiceUtility.chkNull(Constants.ACCESS_CODE).toString(), mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID))
    }

    @SuppressLint("StaticFieldLeak")
    private inner class RenderView : AsyncTask<Void?, Void?, Void?>()
    {

        override fun onPreExecute()
        {
            super.onPreExecute()
            // Showing progress dialog
            bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
        }

        override fun doInBackground(vararg params: Void?): Void? {

            if (ServiceUtility.chkNull(vResponse) != ""
                    && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR") == -1)
            {
                val vEncVal = StringBuffer("")
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent!!.getStringExtra(AvenuesParams.AMOUNT)))
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, ServiceUtility.chkNull(Constants.CURRENCY).toString()))
                encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length - 1), vResponse) //encrypt amount and currency
            }

            return null
        }


        override fun onPostExecute(result: Void?)
        {
            super.onPostExecute(result)
            // Dismiss the progress dialog
            bindingObj.aviProgressBar.setVisibility(View.GONE)

            class MyJavaScriptInterface
            {
                @JavascriptInterface
                fun processHTML(html: String)
                { // process the html source code to get final status of transaction

                    Timber.e("Status: "+ html)
                    var paymentMethod = ""
                    var status: String? = null
                    status =

                    if (html.indexOf("Failure") != -1)
                    {
                        "Transaction Declined!"
                    }

                    else if (html.indexOf("Success") != -1)
                    {
                        "Transaction Successful!"

                    }

                    else if (html.indexOf("Aborted") != -1)
                    {
                        "Transaction Cancelled!"
                    }

                    else
                    {
                        "Status Unknown!"
                    }

                    if(status.equals("Transaction Successful!",ignoreCase = true))
                    {
                        paymentMethod  = parseXml(html)
                      prepareIntent(paymentMethod)

                    }
                    else
                    {

                        val CustomDialog = CustomDialog(this@MembershipCCEvenuePaymentActivity)
                        CustomDialog.setMessage(status)
                        CustomDialog.setPositiveButton("Ok" , object : DialogListenerInterface.onPositiveClickListener {
                            override fun onPositiveClick() {
                                CustomDialog.dismiss()
                                finish()
                            }
                        })
                        CustomDialog.show()

                    }

                }
            }

            bindingObj.webview.settings.javaScriptEnabled = true

            bindingObj.webview.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")

            bindingObj.webview.setWebViewClient(object : WebViewClient()
            {
                override fun onPageFinished(view: WebView, url: String)
                {
                    super.onPageFinished(bindingObj.webview, url)
                    bindingObj.aviProgressBar.setVisibility(View.GONE)
                    if (url.indexOf("/ccavResponseHandler.php") != -1) {
                        bindingObj.webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
                    }
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

                    bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
                }
            })

            try
            {

                Timber.e("State: "+sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_state))
                val postData = (AvenuesParams.ACCESS_CODE + "=" + URLEncoder.encode(ServiceUtility.chkNull(Constants.ACCESS_CODE).toString(), "UTF-8")
                        + "&" + AvenuesParams.MERCHANT_ID + "=" + URLEncoder.encode(ServiceUtility.chkNull(Constants.MERCHANT_ID).toString(), "UTF-8")
                        + "&" + AvenuesParams.ORDER_ID + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID), "UTF-8")
                        + "&" + AvenuesParams.REDIRECT_URL + "=" + URLEncoder.encode(ServiceUtility.chkNull(Constants.REDIRECT_URL).toString(), "UTF-8")
                        + "&" + AvenuesParams.CANCEL_URL + "=" + URLEncoder.encode(ServiceUtility.chkNull(Constants.CANCEL_URL).toString(), "UTF-8")
                        + "&" + AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal, "UTF-8")
                        + "&" + AvenuesParams.BILLING_NAME + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_name), "UTF-8")
                        + "&" + AvenuesParams.BILLING_ADDRESS + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Address), "UTF-8")
                        + "&" + AvenuesParams.BILLING_COUNTRY + "=" + URLEncoder.encode("India", "UTF-8")
                        + "&" + AvenuesParams.BILLING_STATE + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_state), "UTF-8")
                        + "&" + AvenuesParams.BILLING_CITY + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_city), "UTF-8")
                        + "&" + AvenuesParams.BILLING_ZIP + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Zipcode), "UTF-8")
                        + "&" + AvenuesParams.BILLING_TEL + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Phone), "UTF-8")
                        + "&" + AvenuesParams.BILLING_EMAIL + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Email), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_NAME + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_name), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_ADDRESS + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Address), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_COUNTRY + "=" + URLEncoder.encode("India", "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_STATE + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_state), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_CITY + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_city), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_ZIP + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Zipcode), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_TEL + "=" + URLEncoder.encode(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Phone), "UTF-8")
                        )

                bindingObj.webview.postUrl(Constants.TRANS_URL, postData.toByteArray())
            }

            catch (e: UnsupportedEncodingException)
            {
                e.printStackTrace()
            }
        }

    }

    fun get_RSA_key(access_code: String, order_id: String)
    {
        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)


        val apiInterface = RetrofitClient.getClient(Constants.RSA_URL).create(WebServices::class.java)

        val call = apiInterface.getRSAApi(ServiceUtility.chkNull(Constants.ACCESS_CODE).toString(),mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID)
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)
        APIHelper.enqueueWithRetry(call , 5 , object : Callback<ResponseBody>
        {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {

                bindingObj.aviProgressBar.setVisibility(View.GONE)

                if (response.isSuccessful && response.body() != null)
                {

                    vResponse = response.body()!!.string()///save retrieved rsa key

                    if (vResponse!!.contains("!ERROR!"))
                    {
                        val CustomDialog = CustomDialog(this@MembershipCCEvenuePaymentActivity)
                        CustomDialog.setMessage(vResponse!!)
                        CustomDialog.setPositiveButton("Ok" , object : DialogListenerInterface.onPositiveClickListener {
                            override fun onPositiveClick() {
                                CustomDialog.dismiss()
                            }
                        })
                        CustomDialog.show()
                    }
                    else
                    {
                        RenderView().execute() // Calling async task to get display content
                    }
                }

                else
                {
                    val CustomDialog = CustomDialog(this@MembershipCCEvenuePaymentActivity)
                    CustomDialog.setMessage("Oops! Something went wrong. Please try again later.")
                    CustomDialog.setPositiveButton("Ok" , object : DialogListenerInterface.onPositiveClickListener {
                        override fun onPositiveClick() {
                            CustomDialog.dismiss()
                        }
                    })
                    CustomDialog.show()
                }
            }
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                call.cancel()
                bindingObj.aviProgressBar.setVisibility(View.GONE)
                Timber.e("Error:  " + t.message)
            }
        })
    }

    fun prepareIntent(paymentMethod : String)
    {
        val bundle = Bundle()
        bundle.putString("transaction_id", mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID))
        bundle.putString("total_amount",  mainIntent!!.getStringExtra(AvenuesParams.AMOUNT))
        bundle.putString("payment_mode",  paymentMethod)

        val intent = Intent()
        intent.putExtras(bundle)
        setResult(1002 ,intent)
        finish()

    }

    fun parseXml(xmlString: String): String
    {
        var paymentMethodStr = ""
        var paymentMethod = false
        try
        {

            val factory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
            factory.setNamespaceAware(true)
            val xpp: XmlPullParser = factory.newPullParser()
            xpp.setInput(StringReader(xmlString))
            var eventType: Int = xpp.getEventType()

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if (eventType == XmlPullParser.TEXT)
                {

                    if(paymentMethod)
                    {
                        paymentMethodStr =xpp.text
                        Timber.e("Text " + xpp.text) // here you get the text from xml
                        return paymentMethodStr
                    }

                    if (xpp.text.equals("payment_mode", ignoreCase = true))
                        paymentMethod = true

                }

                eventType = xpp.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return paymentMethodStr
    }


    fun show_alert(msg: String) {
        var msg = msg
        val alertDialog = AlertDialog.Builder(
                this@MembershipCCEvenuePaymentActivity).create()
        alertDialog.setTitle("Error!!!")
        if (msg.contains("\n")) msg = msg.replace("\\\n".toRegex(), "")
        alertDialog.setMessage(msg)
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK") { dialog, which -> finish() }
        alertDialog.show()
    }
}
