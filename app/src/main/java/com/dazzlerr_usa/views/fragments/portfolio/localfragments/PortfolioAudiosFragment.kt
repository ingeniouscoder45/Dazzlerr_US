package com.dazzlerr_usa.views.fragments.portfolio.localfragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentPortfolioAudiosBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.adapters.AudiosAdapter
import com.dazzlerr_usa.views.fragments.portfolio.models.PortfolioAudiosModel
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioAudiosPojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.UploadAudioPojo
import com.dazzlerr_usa.views.fragments.portfolio.presenters.PortfolioAudiosPresenter
import com.dazzlerr_usa.views.fragments.portfolio.views.PortfolioAudiosView
import com.google.android.material.snackbar.Snackbar
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class PortfolioAudiosFragment : androidx.fragment.app.Fragment() , PortfolioAudiosView
{
    lateinit var  bindingObj: FragmentPortfolioAudiosBinding
    var adapter:  AudiosAdapter? = null

    lateinit var mPresenter: PortfolioAudiosPresenter
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater , R.layout.fragment_portfolio_audios, container, false)
        initializations()
        apiCalling( (activity as PortfolioActivity).user_id ,currentPage.toString())
        pagination()
        return  bindingObj.root
    }



    fun initializations()
    {
        mPresenter = PortfolioAudiosModel(activity as Activity  , this)
        adapter = AudiosAdapter(activity as Activity, ArrayList() ,this)
        val gManager = LinearLayoutManager(activity)
        bindingObj.documentsRecyclerview.layoutManager = gManager
        bindingObj.documentsRecyclerview.addItemDecoration( DividerItemDecoration(bindingObj.documentsRecyclerview.getContext(), DividerItemDecoration.VERTICAL))
        bindingObj.documentsRecyclerview.adapter = adapter

       if( (activity as PortfolioActivity).navigateFrom.equals("talentFragment",ignoreCase = true))
       {
           bindingObj.errorTxt1.visibility = View.GONE
       }
        else
           bindingObj.errorTxt1.visibility = View.VISIBLE
    }

    private fun pagination()
    {
        bindingObj.documentsRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount

                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading && adapter?.mModelList?.size!! > 1)
                {
                    isLoading = true
                    apiCalling((activity as PortfolioActivity).user_id  , currentPage.toString())
                }
            }
        })
    }

    private fun apiCalling(user_id: String , page:String)
    {

        if(activity?.isNetworkActive()!!)
        {
            mPresenter?.getPortfolioAudios(user_id , page)
        }

        else
        {

            val dialog  = CustomDialog(activity as Activity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
            apiCalling(user_id ,page) })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    fun addItem( list : MutableList<PortfolioAudiosPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            adapter?.removeLoading()

        adapter?.addAll(list)
        adapter?.addLoading()
    }


    fun deleteAudio(document_id:String, position:Int)
    {
        if(activity?.isNetworkActiveWithMessage()!!)
        mPresenter.deleteAudio((activity as PortfolioActivity).user_id ,document_id ,position)
    }

    override fun onAudioDelete(position: Int)
    {
        adapter?.removeItem(position)
        showSnackbar("Audio has been deleted successfully.")

        if(adapter?.mModelList?.size==0)
        {
            bindingObj.emptyLayout.visibility  = View.VISIBLE
        }
    }


    override fun onSuccess(model: PortfolioAudiosPojo)
    {
        if (model.data?.size!=0)
        {
            if(currentPage==PAGE_START)
            {
                adapter?.addAll(model.data!!)
                adapter?.addLoading()
            }
            else
                addItem(model.data!!)

            currentPage++
            isLoading = false
            bindingObj.emptyLayout.visibility  = View.GONE
        }
        else
        {
            if(currentPage!=PAGE_START)
                adapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility  = View.VISIBLE
            }

        }
    }

    override fun onAudioUpload(model: UploadAudioPojo) {

        val Obj = PortfolioAudiosPojo.DataBean()
        Obj.title =   model.data?.title
        Obj.audio_url =   model.data?.audio_url
        Obj.user_id =   model.data?.user_id!!
        Obj.audio_id =   model.data?.audio_id!!
        Obj.created_on =   model.data?.created_on!!
        //adapter?.add(Obj)

        var list : MutableList<PortfolioAudiosPojo.DataBean>  = ArrayList()
        list.addAll(adapter?.mModelList!!)
        list.reverse()
        list.add(Obj)
        list.reverse()
        adapter?.mModelList?.clear()
        adapter?.mModelList?.addAll(list)
        adapter?.notifyDataSetChanged()

        bindingObj.emptyLayout.visibility  = View.GONE
    }

    override fun onFailed(message: String) {
        isLoading =false
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean) {

        if(currentPage==PAGE_START && activity!=null) {
            if (visiblity) {
                (activity as PortfolioActivity).startProgressBarAnim()
            } else {
                (activity as PortfolioActivity).stopProgressBarAnim()
            }
        }
    }
    override fun showProgress(visiblity: Int) {

        if(activity!=null) {
            if (visiblity==1) {
                (activity as PortfolioActivity).startProgressBarAnim()
            } else {
                (activity as PortfolioActivity).stopProgressBarAnim()
            }
        }
    }


    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = (activity as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {


            1004 -> {


                    if( data?.extras!=null)
                    {

                        var bundle = data?.extras
                        if(activity?.isNetworkActiveWithMessage()!!)
                        mPresenter.uploadAudio((activity as PortfolioActivity).user_id, File(bundle?.getString("uri_path").toString()), bundle?.getString("title").toString())

                    }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }
}// Required empty public constructor
