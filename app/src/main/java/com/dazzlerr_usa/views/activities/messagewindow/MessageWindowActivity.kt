package com.dazzlerr_usa.views.activities.messagewindow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityMessageWindowBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.*
import com.dazzlerr_usa.utilities.PermissionUtils
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.report.ProfileReportActivity
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.android.material.snackbar.Snackbar
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import org.json.JSONException
import org.json.JSONObject
import permissions.dispatcher.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@RuntimePermissions
class MessageWindowActivity : AppCompatActivity()  , View.OnClickListener , MessageWindowView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var mPresenter: MessageWindowPresenter
    lateinit var mAdapter: MessageWindowAdapter
    lateinit var bindingObj : ActivityMessageWindowBinding
    var thread_id = ""
    var receiver_id = ""
    var mMessageList: ArrayList<MessageWindowPojo.DataBean> = ArrayList()
    var blocked_by_receiver  =""
    var blocked_by_user  =""
    var mSocket: Socket? =null
    var isPermissionsGiven = false
    var isConnected  = true


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_message_window)
        Timber.e("OnCreate")
        clickListeners()
        initializations()
        registerSocketMethods()
    }

    fun initializations()
    {

        mSocket = (application as MyApplication).getSocket()
        (application as MyApplication).myComponent.inject(this)
        //bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.titletxt.text = intent.extras?.getString("Username")

        thread_id = intent.extras?.getString("thread_id")!!
        receiver_id = intent.extras?.getString("sender_id")!!

        mPresenter = MessageWindowModel(this , this)
        mAdapter = MessageWindowAdapter(this  ,mMessageList)
        bindingObj.messagesRecyclerview.layoutManager = LinearLayoutManager(this)
        bindingObj.messagesRecyclerview.adapter = mAdapter

        if(thread_id.isEmpty())
        {
            if(isNetworkActiveWithMessage())
                mPresenter.generateThread(sharedPreferences.getString(Constants.User_id).toString() , receiver_id)
        }
        else {
            if (isNetworkActiveWithMessage())
                mPresenter.getAllMessages(sharedPreferences.getString(Constants.User_id).toString(), thread_id)
        }
    }

    fun clickListeners()
    {
        bindingObj.btnChatSend.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightbtn.setOnClickListener(this)
        bindingObj.chatAttachmentsBtn.setOnClickListener(this)
    }


    fun askPermissions()
    {
        showImagePickerWithPermissionCheck()
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE )
    fun showImagePicker()
    {
        isPermissionsGiven =true
        imagePickerDialog()
    }

    @OnShowRationale(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageRationale(request: PermissionRequest) {
        PermissionUtils.showRationalDialog(this@MessageWindowActivity, R.string.permission_rationale_camera_storage, request)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageDenied() {
        Toast.makeText(this@MessageWindowActivity, R.string.permission_denied_camera_storage , Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_camera_storage, Constants.REQ_CODE_APP_SETTINGS)
    }

    fun registerSocketMethods()
    {
            mSocket = (application as MyApplication).getSocket()

            mSocket?.on("onDelivered", onMessageDelivered)
            mSocket?.on("onRead", onMessageRead)
            mSocket?.on("on_send_message", onSendMessage)
            mSocket?.on("onBlockStatusChanged", onUserBlocked)
            mSocket!!.on("onOnlineStatusChanged", onOnlineStatusChanged)
            //mSocket?.on("onUserBlocked", onUserBlocked)
            mSocket?.on("onConnect", onConnect)
            mSocket?.on("onDisconnect", onDisconnect)

            mSocket?.connect()
    }

    private val onConnect: Emitter.Listener = Emitter.Listener {
        this@MessageWindowActivity.runOnUiThread(Runnable {


            if(!isConnected) {
                isConnected   = true
                //Toast.makeText(this@MessageWindowActivity, "Connected", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private val onDisconnect: Emitter.Listener = Emitter.Listener {
        this@MessageWindowActivity.runOnUiThread(Runnable
        {

            isConnected = false
            //Toast.makeText(this@MessageWindowActivity, "Disconnected", Toast.LENGTH_SHORT).show()
        })
    }


    private val onUserBlocked: Emitter.Listener =  Emitter.Listener { args ->
        this@MessageWindowActivity.runOnUiThread(Runnable
        {
            val data = args[0] as JSONObject

            if(thread_id.equals(data.getJSONObject("data").getString("thread_id")))
            {
                Timber.e("onUserBlocked: " + data.toString())
                var thread_id: String = ""
                var user_id: String = ""
                try {
                    user_id = data.getJSONObject("data").getString("sender_id")
                    thread_id = data.getJSONObject("data").getString("thread_id")


                    if(user_id.equals(sharedPreferences.getString(Constants.User_id)))
                    {
                        var dialog = CustomDialog(this@MessageWindowActivity)

                        if(blocked_by_user.equals("0"))
                        {
                            blocked_by_user = "1"
                            dialog.setTitle("Blocked!!")
                            dialog.setMessage(""+bindingObj.titleLayout.titletxt.text+ " has been blocked successfully")
                        }

                        else
                        {
                            blocked_by_user = "0"
                            dialog.setTitle("Unblocked!!")
                            dialog.setMessage(""+bindingObj.titleLayout.titletxt.text+" has been unblocked successfully")
                        }
                        dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

                            finish()
                        })

                        dialog.show()
                    }

                    if(user_id.equals(receiver_id))
                    {

                        var dialog = CustomDialog(this@MessageWindowActivity)

                        if(blocked_by_receiver.equals("0"))
                        {
                            blocked_by_receiver = "1"
                            dialog.setTitle("Blocked!")
                            dialog.setMessage("You are blocked by "+ bindingObj.titleLayout.titletxt.text)
                        }

                        else
                        {
                            blocked_by_receiver = "0"
                            dialog.setTitle("Unblocked!")
                            dialog.setMessage("You are unblocked by "+ bindingObj.titleLayout.titletxt.text)
                        }
                        dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

                            finish()
                        })

                        dialog.show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

        })
    }


    private val onOnlineStatusChanged: Emitter.Listener =  Emitter.Listener { args ->
        this@MessageWindowActivity.runOnUiThread(Runnable
        {
            val data = args[0] as JSONObject

            Timber.e("OnlineStatusResponse: " +data.toString())
            var online_status: String = ""
            var user_id: String = ""
            try {

                user_id = data.getJSONObject("data").getString("user_id")
                online_status = data.getJSONObject("data").getString("online_status")

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if(user_id.equals(receiver_id))
            {
                if(online_status.equals("1"))
                {
                    bindingObj.titleLayout.onlineStatusTxt.visibility = View.VISIBLE
                    bindingObj.titleLayout.onlineStatusTxt.text = "Online"
                }

                else
                {
                    bindingObj.titleLayout.onlineStatusTxt.visibility = View.VISIBLE
                    bindingObj.titleLayout.onlineStatusTxt.text = "Away"
                }
            }
            // Toast.makeText(this@MessageWindowActivity, "Disconnected", Toast.LENGTH_SHORT).show()
        })
    }

    private val onMessageDelivered: Emitter.Listener = Emitter.Listener { args ->
        this@MessageWindowActivity.runOnUiThread(Runnable {
            val data = args[0] as JSONObject

            if(data.getString("success").equals("true")) {
                Timber.e("On Message Delivered: " + data.toString())
                if (thread_id.equals(data.getJSONObject("data").getString("thread_id"))) {


                    var message_id: String = ""
                    var thread_id: String = ""
                    try {

                        thread_id = data.getJSONObject("data").getString("thread_id")

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    if (thread_id.equals(this.thread_id, ignoreCase = true)) {

                        for (i in mAdapter.mMessageList.indices) {
                            if (mAdapter.mMessageList.get(i).read_status.equals("0"))
                                mAdapter.mMessageList.get(i).read_status = "1"

                        }
                        mAdapter.notifyDataSetChanged()

                    }
                }

            }
        })
    }

    private val onMessageRead: Emitter.Listener = Emitter.Listener { args ->
        this@MessageWindowActivity.runOnUiThread(Runnable {
            val data = args[0] as JSONObject


            if(thread_id.equals(data.getJSONObject("data").getString("thread_id"))  &&  receiver_id.equals(data.getJSONObject("data").getString("sender_id")))
            {
                Timber.e("On Message Read: "+ data.toString())
                var sender_id: String = ""
                var thread_id: String = ""
                try
                {


                    thread_id = data.getJSONObject("data").getString("thread_id")
                    sender_id = data.getJSONObject("data").getString("sender_id")
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }

                if(thread_id.equals(this.thread_id,ignoreCase = true) && sender_id.equals(receiver_id)) {
                    for (i in mAdapter.mMessageList.indices) {

                        if (mAdapter.mMessageList.get(i).read_status.equals("0") || mAdapter.mMessageList.get(i).read_status.equals("1"))
                            mAdapter.mMessageList.get(i).read_status = "2"
                    }
                    mAdapter.notifyDataSetChanged()

                }
            }
        })
    }

    private val onSendMessage: Emitter.Listener = object : Emitter.Listener
    {
        override fun call(vararg args: Any)
        {
            this@MessageWindowActivity.runOnUiThread(Runnable
            {

/*                val model = MessageWindowPojo.DataBean()
                model.message = bindingObj.msgType.text.toString()
                model.ReadState = "2020-07-31T10:50:15.000Z"
                model.sender_id = sharedPreferences.getString(Constants.User_id).toString()
                sendMessage(model)*/


                val data = args[0] as JSONObject
                Timber.e("Message: "+data)

                if(thread_id.equals(data.getJSONObject("data").getString("thread_id")))
                {
                    isConnected =true

                    var ReadState: String = ""
                    var message: String = ""
                    var sender_id: String = ""
                    var message_id: String = ""
                    var message_type: String = ""
                    var msg_image: String = ""
                    try {
                        val data1 = data.getJSONObject("data") as JSONObject
                        ReadState = data1.getString("ReadState")
                        message = data1.getString("message")
                        sender_id = data1.getString("sender_id")
                        message_id = data1.getString("msg_id")
                        message_type = data1.getString("message_type")
                        msg_image = data1.getString("msg_image")

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    val model = MessageWindowPojo.DataBean()
                    model.message = message
                    model.ReadState = ReadState
                    model.contact_id = message_id
                    model.sender_id = sender_id
                    model.read_status = "0"
                    model.message_type =message_type
                    model.msg_image =msg_image
                    sendMessage(model)

                    if(sender_id.equals(sharedPreferences.getString(Constants.User_id))) {

                        bindingObj.msgType.setText("")
                    }
                    else
                    {
                        // Sending read status to server through sockets
                     readMessage(message_id)
                    }

                    // add the message to view
                    //addMessage(username, message)

                }
            })

        }
    }

    override fun onThreadIdGenrated(model: GenerateThraedPojo)
    {

        thread_id = model.thread_id!!
        if (isNetworkActiveWithMessage())
            mPresenter.getAllMessages(sharedPreferences.getString(Constants.User_id).toString(), thread_id)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun OnGetMessagesSuccess(model: MessageWindowPojo)
    {

        if(model.data!!.size!=0)
        readMessage(model.data!![model.data!!.size-1].contact_id!!)

        blocked_by_user = model.sender_blocked!!
        blocked_by_receiver = model.user_blocked!!

        if(model.online_status.equals("1"))
        {
            bindingObj.titleLayout.onlineStatusTxt.visibility = View.INVISIBLE
            bindingObj.titleLayout.onlineStatusTxt.text = "Online"


            bindingObj.titleLayout.onlineStatusTxt.visibility = View.VISIBLE
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                    bindingObj.titleLayout.onlineStatusTxt,
                    bindingObj.titleLayout.onlineStatusTxt.right + bindingObj.titleLayout.onlineStatusTxt.left / 2,
                    bindingObj.titleLayout.onlineStatusTxt.top + bindingObj.titleLayout.onlineStatusTxt.bottom / 2,
                    0f, bindingObj.titleLayout.onlineStatusTxt.width.toFloat()
            )

            circularReveal.duration = 200
            circularReveal.start()

        }

        else
        {
            bindingObj.titleLayout.onlineStatusTxt.visibility = View.INVISIBLE
            bindingObj.titleLayout.onlineStatusTxt.text = "Away"

            bindingObj.titleLayout.onlineStatusTxt.visibility = View.VISIBLE
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                    bindingObj.titleLayout.onlineStatusTxt,
                    bindingObj.titleLayout.onlineStatusTxt.right + bindingObj.titleLayout.onlineStatusTxt.left / 2,
                    bindingObj.titleLayout.onlineStatusTxt.top + bindingObj.titleLayout.onlineStatusTxt.bottom / 2,
                    0f, bindingObj.titleLayout.onlineStatusTxt.width.toFloat()
            )

            circularReveal.duration = 200
            circularReveal.start()
        }

        this.mMessageList.addAll(model.data!!)

        var localMessagelist: ArrayList<MessageWindowPojo.DataBean> = ArrayList()


        for (i in this.mMessageList.indices)
        {

            Timber.e("Current Date:"+ mAdapter.dateFormat2(mMessageList.get(i).ReadState) + " Previous date: "+mAdapter.PreviousDate )
            if(mAdapter.dateFormat2(mMessageList.get(i).ReadState).equals(mAdapter.PreviousDate))
            {
                mMessageList.get(i).hasDateChanged = false
                localMessagelist.add(mMessageList.get(i))
            }
            else {
                //mMessageList.get(i).hasDateChanged = true
                mAdapter.PreviousDate = mAdapter.dateFormat2(mMessageList.get(i).ReadState)

                var modelObj = MessageWindowPojo.DataBean()
                modelObj.hasDateChanged =  true
                modelObj.ReadState =  mMessageList.get(i).ReadState

                localMessagelist.add(modelObj)

                mMessageList.get(i).hasDateChanged = false
                localMessagelist.add(mMessageList.get(i))
            }

        }

        mMessageList.clear()
        mMessageList.addAll(localMessagelist)
        mAdapter.notifyDataSetChanged()
        bindingObj.messagesRecyclerview.scrollToPosition(mAdapter.mMessageList.size-1)
    }

    override fun onChatReplySuccess(model: MultimediaChatPojo)
    {

        var jsonObject = JSONObject()
        jsonObject.put("thread_id", thread_id)
        jsonObject.put("sender_id", sharedPreferences.getString(Constants.User_id).toString())
        jsonObject.put("receiver_id", receiver_id)
        jsonObject.put("name", sharedPreferences.getString(Constants.User_name).toString())
        jsonObject.put("email", sharedPreferences.getString(Constants.User_Email).toString())
        jsonObject.put("phone", sharedPreferences.getString(Constants.User_Phone).toString())
        jsonObject.put("message", "")
        jsonObject.put("message_type", "image")
        jsonObject.put("msg_id",model.data?.msg_id!! )
        jsonObject.put("sender_pic",model.data?.sender_pic!! )
        jsonObject.put("msg_image", model.data?.msg_image!!)
        jsonObject.put("device_id", DeviceInfoConstants.DEVICE_ID)
        jsonObject.put("device_brand", DeviceInfoConstants.DEVICE_BRAND)
        jsonObject.put("device_model", DeviceInfoConstants.DEVICE_MODEL)
        jsonObject.put("device_type", DeviceInfoConstants.DEVICE_TYPE)
        jsonObject.put("operating_system", DeviceInfoConstants.DEVICE_VERSION)

        mSocket?.emit("send_new_message", jsonObject)

    }

    override fun onUserBlockedSuccess()
    {

        var dialog = CustomDialog(this@MessageWindowActivity)

        if(blocked_by_user.equals("0"))
        {
            blocked_by_user = "1"
            dialog.setTitle("User blocked!!")
            dialog.setMessage("User has been blocked successfully")
        }

        else
        {
            blocked_by_user = "0"
            dialog.setTitle("User unblocked!!")
            dialog.setMessage("User has been unblocked successfully")
        }
        dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

           finish()
        })

        dialog.show()

    }


    fun readMessage(message_id : String)
    {
        var jsonObject  = JSONObject()
        jsonObject.put("thread_id" , thread_id)
        jsonObject.put("sender_id" ,sharedPreferences.getString(Constants.User_id))
        jsonObject.put("read_status" ,"2")
        jsonObject.put("msg_id" , message_id)

        mSocket?.emit("on_message_read", jsonObject )
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean) {

        if(visiblity)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
    }

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
     //   bindingObj.aviProgressBar.show()
        // or avi.smoothToShow();
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
       // bindingObj.aviProgressBar.hide()
        // or avi.smoothToHide();
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
            R.id.leftbtn->
            {
                onBackPressed()
            }

            R.id.chat_attachments_btn->
            {
                if(blocked_by_user.equals("1"))
                {
                    showSnackbar("You have blocked "+ bindingObj.titleLayout.titletxt.text +". Please unblock first to initiate chat.")
                }

                else if(blocked_by_receiver.equals("1"))
                {
                    showSnackbar("Can not send message because you are blocked by "+ bindingObj.titleLayout.titletxt.text)
                }

                else
                {
                    if (isPermissionsGiven)
                        imagePickerDialog()
                    else
                        askPermissions()
                }
            }
            R.id.btn_chat_send->
            {
                if(blocked_by_user.equals("1"))
                {
                    showSnackbar("You have blocked "+ bindingObj.titleLayout.titletxt.text +". Please unblock first to initiate chat.")
                }

                else if(blocked_by_receiver.equals("1"))
                {
                    showSnackbar("Can not send message because you are blocked by "+ bindingObj.titleLayout.titletxt.text)
                }

                else
                {;
                    Timber.e("Text: "+bindingObj.msgType.text.toString())
                    if (isNetworkActiveWithMessage() && bindingObj.msgType.text.toString().isNotEmpty())
                    {

                        var jsonObject = JSONObject()
                        jsonObject.put("thread_id", thread_id)
                        jsonObject.put("sender_id", sharedPreferences.getString(Constants.User_id).toString())
                        jsonObject.put("name", sharedPreferences.getString(Constants.User_name).toString())
                        jsonObject.put("email", sharedPreferences.getString(Constants.User_Email).toString())
                        jsonObject.put("phone", sharedPreferences.getString(Constants.User_Phone).toString())
                        jsonObject.put("message", bindingObj.msgType.text.toString())
                        jsonObject.put("sender_pic", "")
                        jsonObject.put("device_id", DeviceInfoConstants.DEVICE_ID)
                        jsonObject.put("device_brand", DeviceInfoConstants.DEVICE_BRAND)
                        jsonObject.put("device_model", DeviceInfoConstants.DEVICE_MODEL)
                        jsonObject.put("device_type", DeviceInfoConstants.DEVICE_TYPE)
                        jsonObject.put("operating_system", DeviceInfoConstants.DEVICE_VERSION)

                        Timber.e("Connected: "+ mSocket!!.connected() )
                        mSocket?.emit("send_new_message", jsonObject)

                        isConnected =true

                    }
                }
            }


            R.id.rightbtn->
            {

                val popupMenu: PopupMenu = PopupMenu(this, bindingObj.titleLayout.rightlayout)


                    popupMenu.menu.add("Report")

                if(blocked_by_user.equals("0"))
                    popupMenu.menu.add("Block")

                else
                    popupMenu.menu.add("Unblock")


                    popupMenu.setOnMenuItemClickListener { item ->
                        if(item?.title?.equals("Report")!!)
                        {
                            if (isNetworkActiveWithMessage())
                            {
                                val bundle = Bundle()
                                bundle.putString("profile_id", receiver_id)
                                bundle.putString("type", "message")
                                startActivity(Intent(this@MessageWindowActivity, ProfileReportActivity::class.java).putExtras(bundle))
                            }
                        }

                        else if(item?.title?.equals("Block")!!)
                        {
                            blockOrUnblockUser("Block")
                        }

                        else if(item?.title?.equals("Unblock")!!)
                        {
                            if(isNetworkActiveWithMessage())
                            blockOrUnblockUser("Unblock")
                        }

                        true
                    }
                popupMenu.show()
            }
        }
    }

    fun blockOrUnblockUser(type: String)
    {
        var dialog = CustomDialog(this@MessageWindowActivity)

        dialog.setTitle("Alert!")

        if(type.equals("Block"))
        dialog.setMessage("Are you sure that you want to block this user?")

        else
            dialog.setMessage("Are you sure that you want to Unblock this user?")

        dialog.setPositiveButton("Yes", DialogListenerInterface.onPositiveClickListener {

            var jsonObject = JSONObject()
            jsonObject.put("thread_id", thread_id)
            jsonObject.put("sender_id", sharedPreferences.getString(Constants.User_id).toString())


            if(type.equals("Block"))
            {
                jsonObject.put("status", "1")
            }

            else{
                jsonObject.put("status", "0")
            }

            mSocket?.emit("change_block_status" , jsonObject)

            dialog.dismiss()
        })

        dialog.setNegativeButton("No" , DialogListenerInterface.onNegetiveClickListener {

            dialog.dismiss()
        })

        dialog.show()
    }

    fun sendMessage(model : MessageWindowPojo.DataBean)
    {
        mAdapter.add(model)
        bindingObj.messagesRecyclerview.scrollToPosition(mAdapter.mMessageList.size-1)
    }


    fun disConnectSockets()
    {
        mSocket!!.disconnect()
        mSocket!!.off("onDelivered", onMessageDelivered)
        mSocket!!.off("on_message_read", onMessageRead)
        mSocket!!.off("onOnlineStatusChanged", onOnlineStatusChanged)
        mSocket!!.off("onBlockStatusChanged", onUserBlocked)
        mSocket!!.off("on_send_message", onSendMessage)
        mSocket!!.off("onConnect", onConnect)
        mSocket!!.off("onDisconnect", onDisconnect)
    }

    override fun onBackPressed()
    {
        //disConnectSockets()
        super.onBackPressed()

        if(isTaskRoot)
        {
            sharedPreferences.saveString(Constants.User_type , "MainUser")
            val intent = Intent(this, HomeActivity::class.java)
            sharedPreferences.saveString(Constants.Latitude, "28.65381")//Delhi Location
            sharedPreferences.saveString(Constants.Longitude, "77.22897")
            sharedPreferences.saveString(Constants.CurrentAddress, "Delhi")

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
            finish()
        }
    }

    override fun onResume()
    {
        super.onResume()

        Constants.CURRENT_THREAD_ID = thread_id


        /*disConnectSockets()

        mSocket = null
        mSocket = (application as MyApplication).getSocket()
        registerSocketMethods()*/

        //mSocket!!.connect()

        //registerSocketMethods()
        var jsonObject  = JSONObject()
        jsonObject.put("user_id" , sharedPreferences.getString(Constants.User_id).toString())
        jsonObject.put("online_status" ,"1")
        mSocket?.emit("change_online_status", jsonObject )


/*        val handler = Handler()
        handler.postDelayed(Runnable {
            //Do something after 10000ms

        }, 3000)*/

    }

    override fun onPause()
    {
        super.onPause()

        Constants.CURRENT_THREAD_ID = ""

        var jsonObject  = JSONObject()
        jsonObject.put("user_id" , sharedPreferences.getString(Constants.User_id).toString())
        jsonObject.put("online_status" ,"0")
        mSocket?.emit("change_online_status", jsonObject )
    }

    override fun onDestroy() {
        super.onDestroy()

        disConnectSockets()
    }


    fun imagePickerDialog()
    {
        FilePickerBuilder.instance
                .setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .enableCameraSupport(true)
                .pickPhoto(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode)
        {

            FilePickerConst.REQUEST_CODE_PHOTO -> if (resultCode == Activity.RESULT_OK && data != null)
            {
                val photoPaths: ArrayList<String> = ArrayList()
                photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA))
                Timber.e(photoPaths.toString())

                var bundle = Bundle()
                bundle.putString("image_uri" , photoPaths.get(0))

                startActivityForResult(Intent(this@MessageWindowActivity , MediaPreviewActivity::class.java).putExtras(bundle) ,1001)

                //mSocket!!.connect()
              /*  CropImage.activity(Uri.fromFile(File(photoPaths.get(0))))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1 ,1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setFixAspectRatio(true)
                        .setAllowRotation(false)
                        .setAllowCounterRotation(false)
                        .setAllowFlipping(false)
                        .setAutoZoomEnabled(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropMenuCropButtonTitle("Submit")
                        .start(this@MessageWindowActivity)*/

            }

            1001->
            {
                if (data?.extras != null)
                {
                    var bundle: Bundle = data?.extras!!

                    var uri = bundle.getString("image_uri")!!

                    if (isNetworkActiveWithMessage())
                    {
                        mPresenter.sendMultimediaMessage(File(uri), thread_id, sharedPreferences.getString(Constants.User_id).toString()
                                , sharedPreferences.getString(Constants.User_name).toString()
                                , sharedPreferences.getString(Constants.User_Email).toString()
                                , sharedPreferences.getString(Constants.User_Phone).toString())
                    }
                }
            }

        }

    }


}
