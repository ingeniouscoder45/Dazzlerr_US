package com.dazzlerr_usa.views.activities.portfolio.addvideo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAddVideoBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class AddVideoActivity : AppCompatActivity() , View.OnClickListener , VideoView {

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj: ActivityAddVideoBinding
    lateinit var mPresenter: VideoPresenter
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_add_video)
        initializations()
        clickListeners()
    }

    fun initializations() {
        //----------------Injecting dagger into shared Preferences
        (application as MyApplication).myComponent.inject(this)

        bindingObj.titleLayout.titletxt.setText("Add Video")
        mPresenter = VideoModel(this, this)

        bindingObj.videoDecriptionEdittext?.addTextChangedListener(object : TextWatcher
        {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int)
            {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int)
            {
                try
                {
                    bindingObj.characterLeftTxt?.text = "Characters left: " + (300 - s.length)
                }

                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        })
    }

    fun clickListeners() {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.videoSubmitBtn.setOnClickListener(this)
    }


    override fun isValidate() {

      mPresenter.addVideo(sharedPreferences.getString(Constants.User_id).toString()
              , bindingObj.videoTitleEdittext.text.toString().trim()
              , bindingObj.videoDecriptionEdittext.text.toString().trim()
              , bindingObj.videoUrlEdittext.text.toString().trim())
    }

    override fun onSuccess() {
        val dialog = CustomDialog(this@AddVideoActivity)
                dialog.setTitle("Alert!")
                dialog.setMessage("Video has been added successfully.")
                dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

                    sendResult()
                })
                dialog.show()
    }

    override fun onFailed(message: String)
    {

        showSnackbar(message)
    }

    override fun showProgress(showProgress: Boolean) {

        if(showProgress)
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

    fun sendResult()
    {
        val bundle = Bundle()
        bundle.putString("extras" , "true")
        val intent = Intent()
        intent.putExtras(bundle)
        setResult(101, intent)
        finish()
    }

    override fun onClick(v: View?)
    {
        when(v?.id)
        {
            R.id.videoSubmitBtn->
            {
                mPresenter.validate(bindingObj.videoTitleEdittext.text.toString().trim()
                        , bindingObj.videoDecriptionEdittext.text.toString().trim()
                        , bindingObj.videoUrlEdittext.text.toString().trim())
            }

            R.id.leftbtn-> finish()

        }
    }


}
