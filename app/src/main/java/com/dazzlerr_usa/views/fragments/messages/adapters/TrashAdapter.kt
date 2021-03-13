package com.dazzlerr_usa.views.fragments.messages.adapters

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.MessagesItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.databinding.TrashItemLayoutBinding
import com.dazzlerr_usa.views.fragments.messages.MessagesFragment
import com.dazzlerr_usa.views.fragments.messages.MessagesListPojo


class TrashAdapter(var context: Context?, var fragment: Fragment?, var mMessageList: MutableList<MessagesListPojo.DataBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

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
            val binderObject = DataBindingUtil.inflate<TrashItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.trash_item_layout, parent, false)

            return ViewHolder(binderObject)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {

        if(viewHolder is ViewHolder)
        {
        viewHolder.bindingObj.messageBinder = mMessageList[i]
        viewHolder.bindingObj.executePendingBindings()

            if(mMessageList.get(i).deleted_from.equals("inbox"))
                viewHolder.bindingObj.trashMessageTxt.text =  "Do you want to move this message on your inbox?"

            else if(mMessageList.get(i).deleted_from.equals("outbox"))
                viewHolder.bindingObj.trashMessageTxt.text =  "Do you want to move this message on your outbox?"

            viewHolder.bindingObj.undoBtn.setOnClickListener {
                (fragment as MessagesFragment).restoreMessage(mMessageList.get(i).thread_id.toString() , i)
            }
        }
    }

    fun add(response: MessagesListPojo.DataBean)
    {
        mMessageList.add(response)
        notifyItemInserted(mMessageList.size - 1)

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
        mMessageList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mMessageList.size)
    }

    fun removeAll()
    {

        mMessageList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mMessageList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(MessagesListPojo.DataBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mMessageList.size - 1
        val result = getItem(position)

        if (result != null) {
            mMessageList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): MessagesListPojo.DataBean? {
        return mMessageList.get(position)
    }

    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mMessageList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }

     inner class ViewHolder(var bindingObj: TrashItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
