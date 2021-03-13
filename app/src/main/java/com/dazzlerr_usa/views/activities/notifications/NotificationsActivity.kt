package com.dazzlerr_usa.views.activities.notifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.notifications.adapters.NotificationAdapter
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import javax.inject.Inject

class NotificationsActivity : AppCompatActivity() , View.OnClickListener , NotificationsView {


    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj: com.dazzlerr_usa.databinding.ActivityNotificationsBinding
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false
    lateinit var mPresenter: NotficationsPresenter
    lateinit var mAdapter: NotificationAdapter


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_notifications)
        initializations()
        clickListeners()
        notificationRecyclerSettings()
        pagination()
        apiCalling(sharedPreferences.getString(Constants.User_id).toString() ,currentPage.toString() ,true)
    }

    internal fun initializations()
    {
        (application as MyApplication).myComponent.inject(this@NotificationsActivity)

        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.titletxt.text = "Notifications"
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)

        mPresenter = NotificationsModel(this , this)
    }


    fun clickListeners()
    {
        bindingObj.titleLayout.rightbtn.visibility = View.GONE
    }

    fun notificationRecyclerSettings()
    {
        mAdapter = NotificationAdapter(this@NotificationsActivity, ArrayList())
        val gManager = LinearLayoutManager(this@NotificationsActivity)

        bindingObj.notificationsRecycler.layoutManager = gManager
        bindingObj.notificationsRecycler.addItemDecoration( DividerItemDecoration(bindingObj.notificationsRecycler.getContext(), DividerItemDecoration.VERTICAL))
        bindingObj.notificationsRecycler.adapter = mAdapter
    }

    private fun pagination()
    {
        bindingObj.notificationsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount

                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading)
                {
                    isLoading = true
                    apiCalling(sharedPreferences.getString(Constants.User_id).toString() ,currentPage.toString() , false)
                }
            }
        })
    }

    private fun apiCalling(user_id :String, page : String ,  isShowProgressbar:Boolean)
    {

        if(this@NotificationsActivity.isNetworkActive())
        {
            mPresenter.getNotifications(user_id  ,page ,  isShowProgressbar)
        }

        else
        {

            val dialog  = CustomDialog(this@NotificationsActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling(user_id , page , true)
            })

            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()

        }
    }

    fun addItem(list : MutableList<NotificationsPojo.Data>)
    {
        if (currentPage != PAGE_START)
            mAdapter?.removeLoading()

        mAdapter?.addAll(list)
        mAdapter?.addLoading()
    }


    override fun onSuccess(model: NotificationsPojo) {

        if (model.data?.size!=0)
        {

            bindingObj.emptyLayout.visibility = View.GONE
            bindingObj.notificationsRecycler.visibility = View.VISIBLE

            if(currentPage==PAGE_START)
            {
                Timber.e("current Page"+currentPage)
                mAdapter?.removeAll()

                mAdapter?.addAll(model.data!! )
                mAdapter?.addLoading()
            }
            else {

                addItem(model.data!!)
            }

            currentPage++
            isLoading = false
        }

        else
        {
            if(currentPage!=PAGE_START)
                mAdapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility = View.VISIBLE
                bindingObj.notificationsRecycler.visibility = View.GONE
                mAdapter?.removeAll()
            }

        }
    }


    override fun onFailed(message: String) {
        isLoading =false
        showSnackbar(message)
    }


    override fun showProgress(visiblity: Boolean, isShowProgressbar: Boolean) {

        if(isShowProgressbar)
        {
            if (visiblity)
                startProgressBarAnim()
            else
                stopProgressBarAnim()

        }
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
    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn ->
            {
                finish()
            }
        }
    }
}
