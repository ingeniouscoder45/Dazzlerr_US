package com.dazzlerr_usa.views.fragments.messages.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.MessagesItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.views.activities.messagewindow.MessageWindowActivity
import com.dazzlerr_usa.views.fragments.messages.MessagesFragment
import com.dazzlerr_usa.views.fragments.messages.MessagesListPojo


class MessagesAdapter(var context: Context?,var fragment: Fragment?, var mMessageList: MutableList<MessagesListPojo.DataBean>, var messageType:Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    var mFilteredMessageList: MutableList<MessagesListPojo.DataBean> = mMessageList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else
        {
            val binderObject = DataBindingUtil.inflate<MessagesItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.messages_item_layout, parent, false)

            return ViewHolder(binderObject)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {

        if(viewHolder is ViewHolder)
        {
        viewHolder.bindingObj.messageBinder = mFilteredMessageList[i]
        viewHolder.bindingObj.executePendingBindings()

/*            viewHolder.bindingObj.deleteBtn.setOnClickListener {

                (fragment as MessagesFragment).deleteMessage(mMessageList.get(i).thread_id.toString() , i ,messageType)
            }*/

            viewHolder.itemView.setOnClickListener{


                val bundle = Bundle()
                bundle.putString("Username" , mFilteredMessageList[i].name)
                bundle.putString("thread_id" , mFilteredMessageList[i].thread_id.toString())
                bundle.putString("sender_id" , mFilteredMessageList[i].sender_id.toString())
                context?.startActivity(Intent(context , MessageWindowActivity::class.java).putExtras(bundle))
                (fragment as MessagesFragment).closeSearch()
            }
        }

    }

    fun add(response: MessagesListPojo.DataBean)
    {
        mFilteredMessageList.add(response)
        notifyItemInserted(mFilteredMessageList.size - 1)

    }

    fun addAll(Items: MutableList<MessagesListPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeItem(position:Int)
    {
        mFilteredMessageList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mFilteredMessageList.size)
    }

    fun removeAll()
    {

        mFilteredMessageList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mFilteredMessageList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(MessagesListPojo.DataBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mFilteredMessageList.size - 1
        val result = getItem(position)

        if (result != null) {
            mFilteredMessageList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): MessagesListPojo.DataBean? {
        return mFilteredMessageList.get(position)
    }

    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mFilteredMessageList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }

     inner class ViewHolder(var bindingObj: MessagesItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

    fun getFilter(): Filter? {
        return object : Filter() {
            protected override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    mFilteredMessageList = mMessageList
                } else {
                    val filteredList: MutableList<MessagesListPojo.DataBean> = ArrayList()
                    for (row in mMessageList)
                    {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.name?.toLowerCase()?.contains(charString.toLowerCase())!!/* || row.name?.toLowerCase()?.contains(charSequence)!!*/) {
                            filteredList.add(row)
                        }
                    }
                    mFilteredMessageList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mFilteredMessageList
                return filterResults
            }

             override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                mFilteredMessageList = filterResults.values as MutableList<MessagesListPojo.DataBean>

                if(mFilteredMessageList.size==0)
                {
                    (fragment as MessagesFragment).bindingObj.emptyLayout.visibility = View.VISIBLE
                    (fragment as MessagesFragment).bindingObj.messagesRecyclerview.visibility = View.GONE
                    (fragment as MessagesFragment).bindingObj.errorImage.visibility = View.GONE
                    (fragment as MessagesFragment).bindingObj.errorText.text = "No results found for '"+charSequence+"'"
                }

                else
                {
                    (fragment as MessagesFragment).bindingObj.emptyLayout.visibility = View.GONE
                    (fragment as MessagesFragment).bindingObj.messagesRecyclerview.visibility = View.VISIBLE
                    (fragment as MessagesFragment).bindingObj.errorImage.visibility = View.VISIBLE
                }
                // refresh the list with filtered data
                notifyDataSetChanged()


            }
        }
    }
}
