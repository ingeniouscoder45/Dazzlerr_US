package com.dazzlerr_usa.views.activities.blogs.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityBlogDetailsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.views.activities.blogs.models.BlogsDetailsModel
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogDetailsPojo
import com.dazzlerr_usa.views.activities.blogs.presenter.BlogsDetailsPresenter
import com.dazzlerr_usa.views.activities.blogs.views.BlogDetailsView
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.LevelListDrawable
import android.os.AsyncTask
import android.os.Build
import android.view.Window
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.GridLayoutManager
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.blogs.adapters.BlogsDetailsCategoryAdapter
import com.dazzlerr_usa.views.activities.blogs.adapters.BlogsDetailsTagsAdapter
import com.dazzlerr_usa.views.activities.blogs.adapters.SimilarBlogsAdapter
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogListPojo
import com.google.android.flexbox.*
import com.google.android.gms.ads.AdRequest
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject


class BlogDetailsActivity : AppCompatActivity(), View.OnClickListener, BlogDetailsView
{
    @Inject
    lateinit var sharedPreferences:HelperSharedPreferences

    lateinit var bindingObj: ActivityBlogDetailsBinding
    lateinit var mPresenter: BlogsDetailsPresenter
    var BLOG_ID :String  = ""
    var CAT_IDs =""
    var SHAREURL= ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bindingObj  = DataBindingUtil.setContentView(this , R.layout.activity_blog_details)
        initializations()
        clickListeners()
        apiCalling(BLOG_ID)
    }

    fun initializations()
    {

        (application as MyApplication).myComponent.inject(this)
        //Initializing Google ads
/*
        val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
        val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
*/

        //MobileAds.initialize(this) {}

        if(sharedPreferences.getString(Constants.Membership_id).equals("")
                ||sharedPreferences.getString(Constants.Membership_id).equals("0")
                ||sharedPreferences.getString(Constants.Membership_id).equals("1")
                ||sharedPreferences.getString(Constants.Membership_id).equals("6")
        ) {
            val adRequest = AdRequest.Builder().build()
            bindingObj.adView.visibility = View.VISIBLE
            bindingObj.adView.loadAd(adRequest)
            //--------------------------
        }
        mPresenter = BlogsDetailsModel(this , this)
        BLOG_ID = intent.extras?.getString("blog_id" ,"").toString()
        bindingObj.similarBlogsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        val spanCount = 2 // 2 columns
        val spacing = 20 // 50px
        val includeEdge = true
        bindingObj.similarBlogsRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))

        val skillsLayoutManager = FlexboxLayoutManager(this@BlogDetailsActivity)
        skillsLayoutManager.flexDirection = FlexDirection.ROW
        skillsLayoutManager.justifyContent = JustifyContent.FLEX_START
        //layoutManager.alignContent = AlignContent.STRETCH
        skillsLayoutManager.alignItems= AlignItems.STRETCH
        skillsLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.blogDetailsCategoryRecyclerView.layoutManager = skillsLayoutManager

        val interestLayoutManager = FlexboxLayoutManager(this@BlogDetailsActivity)
        interestLayoutManager.flexDirection = FlexDirection.ROW
        interestLayoutManager.justifyContent = JustifyContent.FLEX_START
        //layoutManager.alignContent = AlignContent.STRETCH
        interestLayoutManager.alignItems= AlignItems.STRETCH
        interestLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.blogDetailsTagsRecyclerView.setLayoutManager(interestLayoutManager)

    }

    private fun apiCalling(blog_id:String)
    {

        bindingObj.mainLayout.visibility = View.GONE

        if(isNetworkActive()!!)
        {
            mPresenter?.getBlogsDetails(blog_id)
        }

        else
        {

            AlertDialog.Builder(this@BlogDetailsActivity)
                    .setTitle("No internet Connection!")
                    .setMessage("Please connect to Mobile network or WiFi.")
                    .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, which ->
                        apiCalling(blog_id)
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .show()
        }
    }

    private fun clickListeners()
    {
        bindingObj.backBtn.setOnClickListener(this)
        bindingObj.blogDetailsShareBtn.setOnClickListener(this)
        bindingObj.viewSimilarBlogsBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?)
    {
        when(v?.id)
        {
            R.id.backBtn ->
            {
                finish()
            }

            R.id.viewSimilarBlogsBtn ->
            {
                val bundle = Bundle()
                bundle.putString("type" , "Similar")
                startActivity(Intent(this@BlogDetailsActivity , AllOtherBlogsActivity::class.java).putExtras(bundle))
            }

            R.id.blogDetailsShareBtn ->
            {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, SHAREURL)

                    // (Optional) Here we're setting the title of the content
                    putExtra(Intent.EXTRA_TITLE, "Dazzlerr")
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "Dazzlerr - Connecting Talent");
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

        }
    }

    override fun onSuccess(model: BlogDetailsPojo)
    {
        if (model.result?.size != 0)
        {
            bindingObj.mainLayout.visibility = View.VISIBLE
            bindingObj.binderObj = model.result?.get(0)
            bindingObj.executePendingBindings()

            SHAREURL = model.result?.get(0)?.share_url.toString()
            Glide.with(this@BlogDetailsActivity)
                    .load(model.result?.get(0)?.image_url.toString())
                    .apply(RequestOptions().centerCrop().fitCenter())
                    .thumbnail(0.3f)
                    .placeholder(R.drawable.placeholder)
                    .into(bindingObj.timelineImage )

            simpledateFormat(bindingObj.blogDetailsDateTxt , model.result?.get(0)?.blog_date.toString())


            if(model.result?.get(0)?.blog_content.toString().isNotEmpty())
            {

                bindingObj.blogDetailsContentTxt.setHtml(model.result?.get(0)?.blog_content.toString(), HtmlHttpImageGetter(bindingObj.blogDetailsContentTxt))

            }

            for(i:Int in model.category?.indices!!)
            {
                CAT_IDs = CAT_IDs+","+(model.category.get(i).cat_id).toString()
            }

            bindingObj.blogDetailsCategoryRecyclerView.adapter = BlogsDetailsCategoryAdapter(this@BlogDetailsActivity , model.category!!)
            bindingObj.blogDetailsTagsRecyclerView.adapter = BlogsDetailsTagsAdapter(this@BlogDetailsActivity , model.tags!!)

            if(CAT_IDs.length!=0)
            mPresenter.getSimilarBlogs(CAT_IDs.substring(1 , CAT_IDs.length))
        }
    }



    override fun onGetSimilarPostsSuccess(model: BlogListPojo)
    {
        if(model.result?.size!=0)
        bindingObj.similarBlogsRecyclerView.adapter = SimilarBlogsAdapter(this , model.result!! )
    }

    override fun onFailed(message: String)
    {

        showSnackbar(message)
    }

    override fun showProgress(visibility: Boolean)
    {
        if(visibility)
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

    fun simpledateFormat(view: TextView, date : String)
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("dd MMM yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
            var dateStr = ""

            val calendar = Calendar.getInstance()
            calendar.time = datetime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("hh:mma")

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Today " /*+ timeFormatter.format(datetime)*/
            }
            else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Yesterday "/* + timeFormatter.format(datetime)*/
            }
            else
            {
                dateStr = formatter.format(datetime)
            }

            dateStr = formatter.format(datetime)
            view.text = dateStr
        }

        catch (e:Exception)
        {
            e.printStackTrace()
            view.text = date
        }

    }


    private inner class DrawableImageGetter : Html.ImageGetter
    {
        override fun getDrawable(source: String): Drawable
        {

        val d = LevelListDrawable()
        val  empty = getResources().getDrawable(R.drawable.edittext_border)
        d.addLevel(0, 0, empty)
        d.setBounds(0, 0, empty.intrinsicWidth, empty.intrinsicHeight)
        LoadImage().execute(source, d)
        return d

        }
    }


    @SuppressLint("StaticFieldLeak")
    internal inner class LoadImage : AsyncTask<Any, Void, Bitmap>()
    {

        private var mDrawable: LevelListDrawable? = null

        override fun doInBackground(vararg params: Any): Bitmap? {
            val source = params[0] as String
            mDrawable = params[1] as LevelListDrawable
            //Log.d(FragmentActivity.TAG, "doInBackground $source")
            try {
                val `is` = URL(source).openStream()
                return BitmapFactory.decodeStream(`is`)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(bitmap: Bitmap?)
        {
            if (bitmap != null) {
                val d = BitmapDrawable(bitmap)
                mDrawable!!.addLevel(1, 1, d)

                val display = (( getSystemService(Context.WINDOW_SERVICE) as WindowManager) ).getDefaultDisplay()
                // val width = display.getWidth()
                mDrawable!!.addLevel(0,0,mDrawable)
                mDrawable!!.setBounds(0, 0, bitmap.width, bitmap.height)

                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                val t = bindingObj.blogDetailsContentTxt.getText()
                bindingObj.blogDetailsContentTxt.setText(t)
            }
        }
    }


}
