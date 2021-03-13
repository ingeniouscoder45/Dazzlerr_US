package com.dazzlerr_usa.views.activities.calllogs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityCallLogsBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class CallLogsActivity : AppCompatActivity() , CallLogsView
{
    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj : ActivityCallLogsBinding
    lateinit var mPresenter: CallLogsPresenter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_logs)
        initializations()
    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
        mPresenter = CallLogsModel(this , this)
        bindingObj.callLogsRecycler.layoutManager = LinearLayoutManager(this)

        if(isNetworkActiveWithMessage())
        {
            mPresenter.getCallLogs(sharedPreferences.getString(Constants.User_id).toString())
        }
    }

    override fun onSuccess(model: CallLogsPojo) {

        if (model.data.size!=0)
        bindingObj.callLogsRecycler.adapter = CallLogsAdapter(this, model.data)

    }



    override fun onFailed(message: String) {
        showSnackbar(message)
    }


    override fun showProgress(visiblity: Boolean)
    {

            if (visiblity)
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
        try {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
}