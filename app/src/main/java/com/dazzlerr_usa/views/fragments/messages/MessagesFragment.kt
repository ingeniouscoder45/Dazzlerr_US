package com.dazzlerr_usa.views.fragments.messages


import android.animation.Animator
import android.app.Activity
import android.os.Build
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentMessagesBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.views.activities.messages.MessagesActivity
import com.dazzlerr_usa.views.fragments.messages.adapters.MessagesAdapter
import com.dazzlerr_usa.views.fragments.messages.adapters.TrashAdapter
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.MyApplication
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.fragment_messages.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class MessagesFragment : androidx.fragment.app.Fragment() , MessageListView , View.OnClickListener
{

    internal lateinit var bindingObj: FragmentMessagesBinding
    lateinit var mPresenter: MessagesListPresenter
    var inboxAdapter:  MessagesAdapter? = null
    var trashAdapter:  TrashAdapter? = null
    var inboxList:ArrayList<MessagesListPojo.DataBean> = ArrayList()
    var trashList:ArrayList<MessagesListPojo.DataBean> = ArrayList()
    var mSocket: Socket? =null
    var isConnected = true

    override fun onResume() {
        super.onResume()

        apiCalling()
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater, R.layout.fragment_messages, container, false)
        initializations()
        listeners()
        registerSocketMethods()

        return bindingObj.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initializations()
    {

        mSocket = (activity?.application as MyApplication).getSocket()

        mPresenter = MessageListModel(activity as Activity , this)
        inboxAdapter = MessagesAdapter(activity as Activity,this , inboxList ,1)

        trashAdapter = TrashAdapter(activity as Activity,this, trashList)
        val gManager = LinearLayoutManager(activity)
        bindingObj.messagesRecyclerview.layoutManager = gManager
        bindingObj.messagesRecyclerview.addItemDecoration( DividerItemDecoration(bindingObj.messagesRecyclerview.getContext(), DividerItemDecoration.VERTICAL))
        bindingObj.messagesRecyclerview.adapter = inboxAdapter

        bindingObj.searchInputText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int)
            {

                inboxAdapter?.getFilter()?.filter(s)
            }
        })
    }


    fun apiCalling()
    {

        if(activity?.isNetworkActiveWithMessage()!!)
        {
            mPresenter.getChatList((activity as MessagesActivity).sharedPreferences.getString(Constants.User_id).toString())
        }

    }

    fun listeners()
    {
        bindingObj.leftbtn.setOnClickListener(this)
        bindingObj.closeSearchButton.setOnClickListener(this)
        bindingObj.rightLayout.setOnClickListener(this)

        bindingObj.titletxt.text = "Messages"
        bindingObj.rightbtn.visibility= View.VISIBLE
        bindingObj.rightbtn.setImageResource(R.drawable.ic_search)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
     fun openSearch() {
        search_input_text.setText("")
        search_input_text.requestFocus()
        search_open_view.visibility = View.VISIBLE
        val circularReveal = ViewAnimationUtils.createCircularReveal(
                search_open_view,
                bindingObj.rightLayout.right + bindingObj.rightLayout.left / 2,
        bindingObj.rightLayout.top + bindingObj.rightLayout.bottom / 2,
                0f, search_input_text.width.toFloat()
        )
        circularReveal.duration = 200
        circularReveal.start()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
     fun closeSearch() {
        val circularConceal = ViewAnimationUtils.createCircularReveal(
                search_open_view,
                bindingObj.rightLayout.right + bindingObj.rightLayout.left / 2,
        bindingObj.rightLayout.top + bindingObj.rightLayout.bottom / 2,
                search_input_text.width.toFloat(), 0f
        )

        circularConceal.duration = 200
        circularConceal.start()
        circularConceal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) = Unit
            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationStart(animation: Animator?) = Unit
            override fun onAnimationEnd(animation: Animator?) {
                search_open_view.visibility = View.INVISIBLE
                search_input_text.setText("")
                circularConceal.removeAllListeners()
            }
        })
    }
    fun registerSocketMethods()
    {

        if(activity?.isNetworkActiveWithMessage()!!) {
            mSocket?.connect()
            mSocket?.on("onConnect", onConnect)
            mSocket?.on("onDisconnect", onDisconnect)
            //mSocket?.on("onChatlist", onGetChatlist)
            mSocket?.on("on_send_message", onNewMessage)
        }

    }

    private val onConnect: Emitter.Listener = Emitter.Listener {
        activity?.runOnUiThread(Runnable {


            if(!isConnected) {
                isConnected = true
                //Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show()
            }
            /*var jsonObject  = JSONObject()
            jsonObject.put("user_id" , (activity as MessagesActivity).sharedPreferences.getString(Constants.User_id).toString())

            mSocket?.emit("on_chat_list" , jsonObject)*/

        })
    }

    private val onDisconnect: Emitter.Listener = Emitter.Listener {
        activity?.runOnUiThread(Runnable
        {

            isConnected = false
            //Toast.makeText(activity, "Disconnected", Toast.LENGTH_SHORT).show()
        })
    }



/*
    private val onGetChatlist: Emitter.Listener = object : Emitter.Listener
    {
        override fun call(vararg args: Any)
        {
            activity?.runOnUiThread(Runnable
            {
                val obj = args[0] as JSONObject

                if(obj.getString("user_id").equals((activity as MessagesActivity).sharedPreferences.getString(Constants.User_id))) {
                    showProgress(false)
                    inboxList.clear()



                    Timber.e("Response: " + obj.toString())
                    var model = Gson().fromJson(obj.toString(), MessagesListPojo::class.java)


                    if (model.data?.size != 0) {
                        for (i: Int in model.data?.indices!!) {

                            inboxList.add(model.data!!.get(i))
                        }

                        bindingObj.messagesRecyclerview.visibility = View.VISIBLE
                        bindingObj.emptyLayout.visibility = View.GONE

                        inboxAdapter?.notifyDataSetChanged()
                        bindingObj.messagesRecyclerview.adapter = inboxAdapter

                        if (inboxList.size == 0)
                            emptyLayout()
                    } else {
                        emptyLayout()
                    }

                    // Toast.makeText(this@MessageWindowActivity, "Disconnected", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
*/


    private val onNewMessage: Emitter.Listener = object : Emitter.Listener
    {
        override fun call(vararg args: Any)
        {
            activity?.runOnUiThread(Runnable
            {

                val model = MessagesListPojo.DataBean()


                val data = args[0] as JSONObject
                Timber.e("Message: "+data)

                if(!(activity as MessagesActivity).sharedPreferences.getString(Constants.User_id).equals(data.getJSONObject("data").getString("sender_id"))
                        && (activity as MessagesActivity).sharedPreferences.getString(Constants.User_id).equals(data.getJSONObject("data").getString("receiver_id")))
                {

                    try
                    {
                        val data1 = data.getJSONObject("data") as JSONObject
                        model.thread_id  = data1.getString("thread_id")
                        model.subject = data1.getString("subject")
                        model.sender_id = data1.getString("sender_id")
                        model.receiver_id = data1.getString("receiver_id")
                        model.sender_pic = data1.getString("sender_pic")
                        model.created_on  = data1.getString("ReadState")
                        model.name = data1.getString("name")

                        var threadFound = false
                        for(i in inboxAdapter?.mMessageList?.indices!! )
                        {

                            if(inboxAdapter!!.mMessageList.get(i).thread_id.equals( model.thread_id ))
                            {

                                threadFound = true

/*                                inboxAdapter!!.mMessageList[i] = model
                                inboxAdapter!!.notifyItemChanged(i)

                                if(i!=0) {
                                    inboxAdapter!!.notifyItemMoved(i, 0)
                                }*/


                                inboxAdapter!!.mMessageList.removeAt(i)
                                inboxAdapter!!.notifyItemChanged(i)
                                inboxAdapter!!.mMessageList.reverse()
                                inboxAdapter!!.mMessageList.add(model)
                                inboxAdapter!!.mMessageList.reverse()
                                inboxAdapter!!.notifyItemChanged(0)
                                inboxAdapter!!.notifyItemMoved(i, 0)
                            }
/*                            else{

                                if(threadFound)
                                {
                                    inboxAdapter!!.notifyItemChanged(i)
                                }
                            }*/

                        }

                        Timber.e("Size: "+ inboxAdapter!!.mMessageList.size +"  "+inboxAdapter!!.mMessageList)

                        if(!threadFound)
                        {

                            inboxAdapter!!.mMessageList.reverse()
                            inboxAdapter!!.mMessageList.add(model)
                            inboxAdapter!!.mMessageList.reverse()
                            inboxAdapter!!.notifyItemInserted(0)
                            bindingObj.messagesRecyclerview.scrollToPosition(0)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }




                    // add the message to view
                    //addMessage(username, message)

                }
            })

        }
    }


    fun emptyLayout()
    {
            bindingObj.messagesRecyclerview.visibility =View.GONE
            bindingObj.emptyLayout.visibility =View.VISIBLE
            bindingObj.errorText.text = "Your chat history is empty!"


    }

    override fun onGetTrashSuccess(mMessageList: MutableList<MessagesListPojo.DataBean>)
    {
        trashList.clear()
        if(mMessageList.size!=0)
        {
            bindingObj.emptyLayout.visibility =View.GONE
            bindingObj.messagesRecyclerview.visibility =View.VISIBLE
            trashList.addAll(mMessageList)
        }
        else
        {
            bindingObj.messagesRecyclerview.visibility =View.GONE
            bindingObj.emptyLayout.visibility =View.VISIBLE
            bindingObj.errorText.text = "Your trash is empty!"
        }
        trashAdapter?.notifyDataSetChanged()
        bindingObj.messagesRecyclerview.adapter = trashAdapter
    }

    override fun onGetChatlistSuccess(mMessageList: MutableList<MessagesListPojo.DataBean>) {

        inboxList.clear()



        if (mMessageList?.size != 0) {

            inboxList.addAll(mMessageList)

            bindingObj.messagesRecyclerview.visibility = View.VISIBLE
            bindingObj.emptyLayout.visibility = View.GONE

            inboxAdapter?.notifyDataSetChanged()
            bindingObj.messagesRecyclerview.adapter = inboxAdapter

            if (inboxList.size == 0)
                emptyLayout()
        } else {
            emptyLayout()
        }

    }

    override fun onDeleteSuccess(position: Int, messageType:Int)
    {

        showSnackbar("Message has been deleted Successfully")
        if(messageType==1)
        inboxAdapter?.removeItem(position)

    }

    override fun onRestoreSuccess(position: Int)
    {
        showSnackbar("Message has been restored Successfully")
        trashAdapter?.removeItem(position)
    }


    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun showProgress(visibility: Boolean)
    {

            if (visibility)
            {
                (activity as MessagesActivity).startProgressBarAnim()
            }
            else
            {
                (activity as MessagesActivity).stopProgressBarAnim()
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

    fun deleteMessage(thread_id : String , position: Int, messageType:Int)
    {
        mPresenter.deleteMessages(thread_id ,  position,messageType)
    }

    fun restoreMessage(thread_id : String , position: Int)
    {
        mPresenter.restoreMessages(thread_id ,  position)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View) {

        when (v.id)
        {
            R.id.leftbtn -> activity?.onBackPressed()

            R.id.rightLayout->
            {
                openSearch()
            }

            R.id.close_search_button->
            {
                closeSearch()
            }
        }
    }


    override fun onDestroy()
    {
        super.onDestroy()
        mSocket!!.disconnect()
        mSocket!!.off("onConnect", onConnect)
        mSocket!!.off("onDisconnect", onDisconnect)
        mSocket?.off("on_send_message", onNewMessage)
    }
}// Required empty public constructor
