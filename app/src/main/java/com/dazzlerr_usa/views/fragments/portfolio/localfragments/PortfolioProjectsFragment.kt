package com.dazzlerr_usa.views.fragments.portfolio.localfragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentPortfolioProjectsBinding
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.adapters.ProjectsAdapter
import com.dazzlerr_usa.views.fragments.portfolio.models.PortfolioProjectsModel
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioProjectsPojo
import com.dazzlerr_usa.views.fragments.portfolio.presenters.PortfolioProjectsPresenter
import com.dazzlerr_usa.views.fragments.portfolio.views.PortfolioProjectsView
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive

/**
 * A simple [Fragment] subclass.
 */
class PortfolioProjectsFragment : androidx.fragment.app.Fragment() , PortfolioProjectsView
{

    lateinit var  bindingObj: FragmentPortfolioProjectsBinding
    var adapter:  ProjectsAdapter? = null

    lateinit var mPresenter: PortfolioProjectsPresenter
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater , R.layout.fragment_portfolio_projects, container, false)
        initializations()
        pagination()
        apiCalling( (activity as PortfolioActivity).user_id ,currentPage.toString())

        return bindingObj.root
    }

    fun initializations()
    {
        mPresenter = PortfolioProjectsModel(activity as Activity  , this)
        adapter = ProjectsAdapter(activity as Activity , ArrayList() , this)
        val gManager = LinearLayoutManager(activity)
        bindingObj.projectsRecyclerview.layoutManager = gManager
        //bindingObj.projectsRecyclerview.addItemDecoration( DividerItemDecoration(bindingObj.projectsRecyclerview.getContext(), DividerItemDecoration.VERTICAL))
        bindingObj.projectsRecyclerview.adapter = adapter
    }

    private fun pagination()
    {
        bindingObj.projectsRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount
                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading&& adapter?.mModelList?.size!! > 1)
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
            mPresenter?.getPortfolioProjects(user_id , page)
        }

        else
        {

            val dialog  = CustomDialog(activity as Activity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
            apiCalling(user_id ,page)
                    })
                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }

    fun addItem( list : MutableList<PortfolioProjectsPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            adapter?.removeLoading()

        adapter?.addAll(list)
        adapter?.addLoading()
    }

    override fun onSuccess(model: PortfolioProjectsPojo)
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

    fun deleteProject(project_id:String , position:Int)
    {
        mPresenter.deleteProject((activity as PortfolioActivity).user_id ,project_id ,position)
    }

    override fun onProjectDelete(position: Int)
    {
        adapter?.removeItem(position)
        showSnackbar("Project has been deleted successfully.")
    }

    override fun onFailed(message: String)
    {
        isLoading =false
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean)
    {

        if(currentPage==PAGE_START && activity!=null)
        {
            if (visiblity)
            {
                (activity as PortfolioActivity).startProgressBarAnim()
            }
            else
            {
                (activity as PortfolioActivity).stopProgressBarAnim()
            }
        }
    }

    fun showSnackbar(message: String)
    {
        try
        {
            val parentLayout = (activity as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {

        if(requestCode==100)
        {
            if(data?.extras != null && data.extras!!.containsKey("extras"))
            {
                adapter?.removeAll()
                currentPage =PAGE_START
                apiCalling( (activity as PortfolioActivity).user_id ,currentPage.toString())
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }

}// Required empty public constructor
