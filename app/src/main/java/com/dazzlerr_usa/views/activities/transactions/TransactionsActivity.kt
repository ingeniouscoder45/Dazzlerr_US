package com.dazzlerr_usa.views.activities.transactions

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityTransactionsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject


class TransactionsActivity : AppCompatActivity() ,View.OnClickListener  , TransactionsView
{

    @Inject
    lateinit var sharedPreferences:HelperSharedPreferences
    lateinit var bindingObj: ActivityTransactionsBinding
    lateinit var mPresenter: TransactionsPresenter
    lateinit var mAdapter : TransactionsAdapter
    val mTransactionsList: MutableList<TransactionsPojo.Data> ?= ArrayList()
    val PAGE_START = 1
    var currentPage = PAGE_START
    var isLoading = false


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_transactions)
        initializations()
        clickListeners()
        transactionsRecyclerSettings()
        pagination()
        apiCalling(currentPage.toString() , true)
    }

    fun initializations()
    {
        mPresenter  = TransactionsModel(this , this)
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "My Transactions"
    }

    private fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }

    fun transactionsRecyclerSettings()
    {
        mAdapter = TransactionsAdapter(this , mTransactionsList!!)
        val gManager = LinearLayoutManager(this)
        bindingObj.transactionRecyclerView.layoutManager = gManager
        bindingObj.transactionRecyclerView.adapter = mAdapter
    }

    private fun pagination()
    {
        bindingObj.transactionRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount

                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading)
                {
                    isLoading = true
                    apiCalling( currentPage.toString(),false)
                }
            }
        })
    }


    fun apiCalling(page: String ,shouldShowProgress : Boolean)
    {

        if(isNetworkActive())
        {
            mPresenter.getTransactions(sharedPreferences.getString(Constants.User_id).toString() ,page , shouldShowProgress)
        }

        else
        {

            AlertDialog.Builder(this)
                    .setTitle("No internet Connection!")
                    .setMessage("Please connect to Mobile network or WiFi.")
                    .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, which ->
                        apiCalling(page,shouldShowProgress)
                    })

                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .show()
        }
    }

    fun addItem( list : MutableList< TransactionsPojo.Data>)
    {
        if (currentPage != PAGE_START)
            mAdapter.removeLoading()

        mAdapter.addAll(list)
        mAdapter.addLoading()
    }

    override fun onSuccess(model: TransactionsPojo)
    {

        if (model.data?.size!=0)
        {

            if(currentPage==PAGE_START)
            {
                mAdapter.removeAllItems()
                mAdapter.addAll(model.data!!)
                mAdapter.addLoading()
            }
            else
                addItem(model.data!!)

            currentPage++
            isLoading = false

            bindingObj.emptyCartLayout.visibility = View.GONE
            bindingObj.transactionRecyclerView.visibility = View.VISIBLE
        }
        else
        {
            if(currentPage!=1)
                mAdapter.removeLoading()

            else if(currentPage==1)
            {
                bindingObj.emptyCartLayout.visibility = View.VISIBLE
                bindingObj.transactionRecyclerView.visibility = View.GONE

            }
        }
    }

    override fun onFailed(message: String) {
        isLoading =false
        showSnackbar(message)
    }


    override fun showProgress(visiblity: Boolean, isShowProgressbar: Boolean) {

        if (visiblity)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
    }

    fun startProgressBarAnim()
    {
        bindingObj.aviProgressBar.visibility = View.VISIBLE
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.visibility = View.GONE
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout!!, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {

        when(v?.id)
        {
            R.id.leftbtn ->
            {
                finish()
            }

        }
    }



    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }


}
