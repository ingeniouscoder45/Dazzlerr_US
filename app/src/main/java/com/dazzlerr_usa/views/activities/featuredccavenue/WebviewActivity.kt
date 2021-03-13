package com.dazzlerr_usa.views.activities.featuredccavenue
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
import com.dazzlerr_usa.utilities.ccavenueutility.*
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient.getClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import okhttp3.ResponseBody
import org.json.JSONObject
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

class WebviewActivity : AppCompatActivity() {

    var mainIntent: Intent? = null
    var encVal: String? = ""
    var vResponse: String? = null
    lateinit var bindingObj: ActivityWebviewBinding
    var payment_mode = ""

    public override fun onCreate(bundle: Bundle?)
    {
        super.onCreate(bundle)
        bindingObj  = DataBindingUtil.setContentView(this , R.layout.activity_webview)
        mainIntent = intent
        Timber.e("Amount " + mainIntent!!.getStringExtra(AvenuesParams.AMOUNT))
        //get rsa key method
        get_RSA_key(mainIntent!!.getStringExtra(AvenuesParams.ACCESS_CODE), mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID))
    }

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
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent!!.getStringExtra(AvenuesParams.CURRENCY)))
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

                        payment_mode = parseXml(html)

                      prepareIntent()

                    }
                    else
                    {

                        val CustomDialog = CustomDialog(this@WebviewActivity)
                        CustomDialog.setMessage(status)
                        CustomDialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {
                            CustomDialog.dismiss()
                            finish()
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


                val postData = (AvenuesParams.ACCESS_CODE + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.ACCESS_CODE), "UTF-8")
                        + "&" + AvenuesParams.MERCHANT_ID + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.MERCHANT_ID), "UTF-8")
                        + "&" + AvenuesParams.ORDER_ID + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID), "UTF-8")
                        + "&" + AvenuesParams.REDIRECT_URL + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.REDIRECT_URL), "UTF-8")
                        + "&" + AvenuesParams.CANCEL_URL + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.CANCEL_URL), "UTF-8")
                        + "&" + AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal, "UTF-8")
                        + "&" + AvenuesParams.BILLING_NAME + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.BILLING_NAME), "UTF-8")
                        + "&" + AvenuesParams.BILLING_ADDRESS + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.BILLING_ADDRESS), "UTF-8")
                        + "&" + AvenuesParams.BILLING_COUNTRY + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.BILLING_COUNTRY), "UTF-8")
                        + "&" + AvenuesParams.BILLING_STATE + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.BILLING_STATE), "UTF-8")
                        + "&" + AvenuesParams.BILLING_CITY + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.BILLING_CITY), "UTF-8")
                        + "&" + AvenuesParams.BILLING_ZIP + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.BILLING_ZIP), "UTF-8")
                        + "&" + AvenuesParams.BILLING_TEL + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.BILLING_TEL), "UTF-8")
                        + "&" + AvenuesParams.BILLING_EMAIL + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.BILLING_EMAIL), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_NAME + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_NAME), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_ADDRESS + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_ADDRESS), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_COUNTRY + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_COUNTRY), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_STATE + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_STATE), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_CITY + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_CITY), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_ZIP + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_ZIP), "UTF-8")
                        + "&" + AvenuesParams.DELIVERY_TEL + "=" + URLEncoder.encode(mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_TEL), "UTF-8"))

                bindingObj.webview.postUrl(Constants.TRANS_URL, postData.toByteArray())
            }

            catch (e: UnsupportedEncodingException)
            {
                e.printStackTrace()
            }
        }

    }

    fun get_RSA_key(ac: String, od: String) {
        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)


        val apiInterface = getClient(Constants.RSA_URL).create(WebServices::class.java)

        val call = apiInterface.getRSAApi(mainIntent!!.getStringExtra(AvenuesParams.ACCESS_CODE) ,mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID)
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(call , 5 , object : Callback<ResponseBody>
        {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                bindingObj.aviProgressBar.setVisibility(View.GONE)
                if (response.isSuccessful == true && response.body() != null)
                {

                    vResponse = response.body()!!.string()///save retrived rsa key

                    if (vResponse!!.contains("!ERROR!"))
                    {
                        val CustomDialog = CustomDialog(this@WebviewActivity)
                        CustomDialog.setMessage(vResponse!!)
                        CustomDialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {
                            CustomDialog.dismiss()
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
                    val CustomDialog = CustomDialog(this@WebviewActivity)
                    CustomDialog.setMessage("Oops! Something went wrong. Please try again later.")
                    CustomDialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {
                        CustomDialog.dismiss()
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

    fun prepareIntent()
    {
        //--- Start of billing address
        val billingAddress  = JSONObject()
        billingAddress.put("first_name" , mainIntent!!.getStringExtra(AvenuesParams.BILLING_NAME))
        billingAddress.put("last_name" , "")
        billingAddress.put("address_1" , mainIntent!!.getStringExtra(AvenuesParams.BILLING_ADDRESS))
        billingAddress.put("address_2" , "")
        billingAddress.put("city" , mainIntent!!.getStringExtra(AvenuesParams.BILLING_CITY))
        billingAddress.put("state" , mainIntent!!.getStringExtra(AvenuesParams.BILLING_STATE))
        billingAddress.put("postcode" , mainIntent!!.getStringExtra(AvenuesParams.BILLING_ZIP))
        billingAddress.put("country" , "IN")
        billingAddress.put("email" ,  mainIntent!!.getStringExtra(AvenuesParams.BILLING_EMAIL))
        billingAddress.put("phone" ,  mainIntent!!.getStringExtra(AvenuesParams.BILLING_TEL))
        //--- end of billing address

        //--- Start of shipping address
        val shippingAddress  = JSONObject()
        shippingAddress.put("first_name" , mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_NAME))
        shippingAddress.put("last_name" , "")
        shippingAddress.put("address_1" , mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_ADDRESS))
        shippingAddress.put("address_2" , "")
        shippingAddress.put("city" , mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_CITY))
        shippingAddress.put("state" , mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_STATE))
        shippingAddress.put("postcode" , mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_ZIP))
        shippingAddress.put("country" , "IN")
        shippingAddress.put("email" ,  mainIntent!!.getStringExtra(AvenuesParams.BILLING_EMAIL))
        shippingAddress.put("phone" ,  mainIntent!!.getStringExtra(AvenuesParams.DELIVERY_TEL))
        //--- end of billing address

        //--- Start of line items
        val line_item  = JSONObject()
        line_item.put("product_id" ,  mainIntent!!.getStringExtra(AvenuesParams.PRODUCT_ID))
        line_item.put("quantity" , "1")
        line_item.put("product_name" , "Featured")
        line_item.put("valid_till" ,  mainIntent!!.getStringExtra(AvenuesParams.VALID_TILL))
        line_item.put("membership_id" ,  mainIntent!!.getStringExtra(AvenuesParams.MEMBERSHIP_ID))
        //--- end of billing address

        val bundle = Bundle()
        val intent = Intent(applicationContext, StatusActivity::class.java)
        bundle.putString("billing", billingAddress.toString())
        bundle.putString("shipping", shippingAddress.toString())
        bundle.putString("line_item", "["+line_item.toString()+"]")
        bundle.putString("transaction_id", mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID))
        bundle.putString("user_id", mainIntent!!.getStringExtra(AvenuesParams.USER_ID))
        bundle.putString("order_total",  mainIntent!!.getStringExtra(AvenuesParams.AMOUNT))
        bundle.putString("wp_user_id",  mainIntent!!.getStringExtra(AvenuesParams.WP_USER_ID))
        bundle.putString("payment_mode", payment_mode )

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK

        intent.putExtras(bundle)
        startActivity(intent)

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
                this@WebviewActivity).create()
        alertDialog.setTitle("Error!!!")
        if (msg.contains("\n")) msg = msg.replace("\\\n".toRegex(), "")
        alertDialog.setMessage(msg)
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK") { dialog, which -> finish() }
        alertDialog.show()
    }
}
