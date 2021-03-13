package com.dazzlerr_usa.views.fragments.portfolio.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.AudiosItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.portfolio.AudioPlayerActivity
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioAudiosFragment
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioAudiosPojo
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class AudiosAdapter(val mContext: Context, var mModelList: MutableList<PortfolioAudiosPojo.DataBean>, val mFragment : Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else
        {
            val bindingObj = DataBindingUtil.inflate<AudiosItemLayoutBinding>(LayoutInflater.from(mContext), R.layout.audios_item_layout, parent, false)
            return MyViewHolder(bindingObj)
        }

        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if(holder is MyViewHolder) {

            holder.bindingObj.audioTitle.text = mModelList.get(position).title
            simpledateFormat(holder.bindingObj.audioDateTxt ,  mModelList.get(position).created_on.toString())

            if(mContext is PortfolioActivity)
            {

                if (!mContext.navigateFrom!!.equals("talentFragment", ignoreCase = true))
                {

                    holder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                        override fun onLongClick(v: View?): Boolean {

                            deleteProduct(position)
                            return true
                        }
                    })
                }

            }


            holder.bindingObj.audioPlayBtn.setOnClickListener {

                if(mContext.isNetworkActiveWithMessage())
                {

                    var mSongList:ArrayList<HashMap<String , String>> = ArrayList()

                    for(i in mModelList.indices)
                    {
                        var map  : HashMap<String , String> = HashMap()

                        map.put("songTitle" , mModelList.get(i).title.toString())
                        map.put("songPath", mModelList.get(i).audio_url.toString())
                        mSongList.add(map)
                    }

                    var bundle = Bundle()
                    bundle.putInt("songIndex" , position )

                    var intent = Intent(mContext , AudioPlayerActivity::class.java)
                    intent.putExtra("songList" , mSongList)
                    intent.putExtras(bundle)

                    mContext.startActivity(intent)

/*                    Toast.makeText(mContext , "Playing "+ mModelList.get(position).title, Toast.LENGTH_SHORT).show()
                    val url = mModelList.get(position).audio_url
                    val a = Uri.parse(mModelList.get(position).audio_url)

                    val extention: String = url?.substring(url?.lastIndexOf(".")!!)!!
                    Timber.e("Extention: " + extention)
                    val viewMediaIntent = Intent()
                    viewMediaIntent.action = Intent.ACTION_VIEW
                    viewMediaIntent.setDataAndType(a, "audio/"+extention)
                    viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    mContext.startActivity(viewMediaIntent)*/
                }
            }


        }
    }

    fun deleteProduct(position: Int)
    {

        val dialog  = CustomDialog(mContext)
        dialog.setTitle("Alert!")
        dialog.setMessage("Are you sure that you want to delete this audio?")
        dialog.setPositiveButton("Yes, Delete it", DialogListenerInterface.onPositiveClickListener {
            dialog.dismiss()
            (mFragment as PortfolioAudiosFragment).deleteAudio(mModelList.get(position).audio_id.toString() ,position )
        })
        dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
            dialog.dismiss()
        })
        dialog.show()


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
    fun add(response: PortfolioAudiosPojo.DataBean)
    {

        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)
    }

    fun addAll(Items: MutableList<PortfolioAudiosPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)

        }
    }

    override fun getItemCount(): Int
    {
        return mModelList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(PortfolioAudiosPojo.DataBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mModelList.size - 1
        val result = getItem(position)

        if (result != null) {
            mModelList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun removeItem(position: Int)
    {
        mModelList.removeAt(position)
        notifyDataSetChanged()
    }


    fun getItem(position: Int): PortfolioAudiosPojo.DataBean? {
        return mModelList.get(position)
    }


    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mModelList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }


    inner class MyViewHolder(var bindingObj: AudiosItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}