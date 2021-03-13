package com.dazzlerr_usa.views.activities.institutedetails

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityInstituteDetailsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.eventdetails.ContactNumberAdapter
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*

class InstituteDetailsActivity : AppCompatActivity()  ,View.OnClickListener , InstituteDetailsView
{

    lateinit var bindingObj : ActivityInstituteDetailsBinding
    lateinit var mPresenter: InstituteDetailsPresenter
    var institute_id = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj  =DataBindingUtil.setContentView(this ,R.layout.activity_institute_details)

        initializations()
        clickListeners()
        apiCalling(institute_id)
    }


    fun initializations()
    {

        institute_id = intent.extras!!.getString("institute_id","")
        Timber.e("institute_id : " + institute_id)
        mPresenter = InstituteDetailsModel(this , this)
        bindingObj.eventDetailsMainLayout.visibility= View.GONE

    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightbtn.setOnClickListener(this)
        bindingObj.emailBtn.setOnClickListener(this)
        bindingObj.websiteBtn.setOnClickListener(this)
    }

    private fun apiCalling(institute_id:String)
    {

        if(this@InstituteDetailsActivity?.isNetworkActive()!!)
        {
            mPresenter?.getInstituteDetails(institute_id)
        }

        else
        {
            val dialog = CustomDialog(this@InstituteDetailsActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling( institute_id)
            })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    override fun onSuccess(model: InstituteDetailsPojo)
    {
        if (model.data?.size != 0)
        {

            bindingObj.binderObj = model.data[0]!!
            bindingObj.executePendingBindings()
            bindingObj.eventDetailsMainLayout.visibility= View.VISIBLE


            bindingObj.titleLayout.titletxt.text = model.data?.get(0)?.institute_name

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bindingObj.instituteDetails.setText(Html.fromHtml(model.data?.get(0)?.description, Html.FROM_HTML_MODE_LEGACY));
            }

            else
            {
                bindingObj.instituteDetails.setText(Html.fromHtml(model.data?.get(0)?.description));
            }

            Glide.with(this@InstituteDetailsActivity)
                    .load("https://www.dazzlerr.com/API/" + model.data?.get(0)?.institute_image.toString())
                    .apply(RequestOptions().centerCrop())
                    .thumbnail(0.3f)
                    .placeholder(R.drawable.event_placeholder)
                    .into(bindingObj.instituteImage)

            var mContactList : MutableList<String> = ArrayList()

            if(model.data?.get(0)?.institute_phone!=null && !model.data?.get(0)?.institute_phone.equals(""))
                mContactList.add(model.data?.get(0)?.institute_phone!!)


            if(model.data?.get(0)?.phone2!=null && !model.data?.get(0)?.phone2.equals(""))
                mContactList.add(model.data?.get(0)?.phone2!!)


            if(model.data?.get(0)?.phone3!=null && !model.data?.get(0)?.phone3.equals(""))
                mContactList.add(model.data?.get(0)?.phone3!!)


            var contactNumberAdapter = ContactNumberAdapter(this ,mContactList )
            bindingObj.contactRecyclerView.layoutManager = LinearLayoutManager(this@InstituteDetailsActivity , LinearLayoutManager.HORIZONTAL ,false)
            bindingObj.contactRecyclerView.adapter = contactNumberAdapter


            if(model?.gallery?.size!=0) {
                bindingObj.errorGalleyTxt.visibility =View.GONE
                bindingObj.GaleryRecyclerview.visibility =View.VISIBLE
                val mPortfolioAdapter = InstitutesGalleryAdapter(this@InstituteDetailsActivity, model?.gallery!!)
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                bindingObj.GaleryRecyclerview.setLayoutManager(staggeredGridLayoutManager)
                bindingObj.GaleryRecyclerview.adapter = mPortfolioAdapter
            }

            else
            {
                bindingObj.errorGalleyTxt.visibility =View.VISIBLE
                bindingObj.GaleryRecyclerview.visibility =View.GONE
            }


            if(model?.courses.size!=0)
            {
                bindingObj.courseTitleLayout.visibility = View.VISIBLE
                bindingObj.errorCoursesTxt.visibility = View.GONE
                bindingObj.coursesRecyclerView.visibility = View.VISIBLE

                val mCoursesAdapter = InstitutesCousesAdapter(this@InstituteDetailsActivity, model?.courses!!)
                val layoutManager = LinearLayoutManager(this@InstituteDetailsActivity)
                bindingObj.coursesRecyclerView.setLayoutManager(layoutManager)
                bindingObj.coursesRecyclerView.adapter = mCoursesAdapter

            }
            else
            {
               bindingObj.courseTitleLayout.visibility = View.GONE
               bindingObj.errorCoursesTxt.visibility = View.VISIBLE
               bindingObj.coursesRecyclerView.visibility = View.GONE
            }


        }
    }


    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean)
    {

        if(visiblity)
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


    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn-> finish()


            R.id.websiteBtn->
            {

                var url = bindingObj.binderObj?.website.toString()

                if(!url.equals("") && !url.equals("null"))
                {
                    try
                    {
                        if (!url.startsWith("https://") && !url.startsWith("http://"))
                        {
                            url = "http://"+url
                        }
                        val openUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(openUrlIntent)

                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                }

            }

            R.id.emailBtn->
            {
                if(!bindingObj.binderObj?.institute_email!!.equals("") && !bindingObj.binderObj?.institute_email!!.equals("null")) {
                    try {
                        val emailIntent = Intent(Intent.ACTION_SENDTO)
                        emailIntent.data = Uri.parse("mailto:"+bindingObj.binderObj?.institute_email!!)
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }




        }
    }


}