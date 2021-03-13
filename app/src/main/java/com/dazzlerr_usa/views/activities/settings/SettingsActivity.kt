package com.dazzlerr_usa.views.activities.settings

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivitySettingsBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.location.LocationActivity
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import javax.inject.Inject
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.view.WindowManager
import android.widget.*
import com.dazzlerr_usa.views.activities.changepassword.ChangePasswordActivity
import timber.log.Timber
import android.view.Gravity
import androidx.recyclerview.widget.GridLayoutManager
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener
import com.dazzlerr_usa.views.activities.editprofile.models.SetUsernameModel
import com.dazzlerr_usa.views.activities.editprofile.presenters.SetUsernamePresenter
import com.dazzlerr_usa.views.activities.editprofile.views.SetUserrnameView
import com.github.ybq.android.spinkit.SpinKitView


class SettingsActivity : AppCompatActivity() , View.OnClickListener   , SettingsView , SetUserrnameView
{

    @Inject
    lateinit var sharedPreferences : HelperSharedPreferences
    lateinit var bindingObj: ActivitySettingsBinding
    lateinit var mPresenter: SettingsPresenter
    lateinit var mUsernamePresenter: SetUsernamePresenter

    val LOCATIONREQUESTCODE = 100
    var mQuestionList:ArrayList<String> = ArrayList()
    var mQuestionIdsList:ArrayList<String> = ArrayList()
    var selectedAnswer =""
    var selectedQuestion_id = ""
    var selectedQuestion = ""
    lateinit var answerTxt: TextView
    lateinit var aviDialogProgressBar: SpinKitView

    var categoryIdsList : java.util.ArrayList<String> = java.util.ArrayList()
    var mRoleList : java.util.ArrayList<HashMap<String , String>> = java.util.ArrayList()
    var filter_category_list  : java.util.ArrayList<Int> = java.util.ArrayList()

    var role_id  = ""
    var role_name  = ""
    var primary_role = ""
    var seconday_role   =""

    var isUsernameAvailable = false
    lateinit var aviUsernameDialogProgressBar: SpinKitView
    lateinit var usernameMessageTxt : TextView
    lateinit var usernameDialog  :Dialog
    lateinit var validateUsernameBtn  :Button

    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_settings)
        initializations()
        if(sharedPreferences.getString(Constants.Membership_id).equals("2") || sharedPreferences.getString(Constants.Membership_id).equals("3"))
        {
            populateCategories()
            bindingObj.profileRoleLayout.visibility = View.VISIBLE
        }

        else
            bindingObj.profileRoleLayout.visibility = View.GONE

        clickListeners()
        apiCalling(sharedPreferences.getString(Constants.User_id).toString() ,sharedPreferences.getString(Constants.User_Role).toString() )

    }

    internal fun initializations()
    {

        (application as MyApplication).myComponent.inject(this)

        mPresenter = SettingsModel(this , this)
        mUsernamePresenter = SetUsernameModel(this as Activity , this)

        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.titletxt.text = "Settings"

        bindingObj.locationTxt.setSelected(true);
        bindingObj.locationTxt.text  = sharedPreferences.getString(Constants.CurrentAddress)

        if(sharedPreferences.getString(Constants.Membership_id).equals("0")) {
            bindingObj.settingsUsernameLayout.visibility = View.GONE
        }
        else
        {
            bindingObj.settingsUsernameLayout.visibility = View.VISIBLE
        }

        bindingObj.notificationSwitch.isChecked = sharedPreferences.getBoolean(Constants.isShowNotifications)

        bindingObj.hideProfileSwitch.isChecked = sharedPreferences.getString(Constants.IsProfile_published).equals("0")

        if(sharedPreferences.getString(Constants.IsProfile_published).equals("1"))
            bindingObj.hideProfileSwitch.isChecked = false

        else if(sharedPreferences.getString(Constants.IsProfile_published).equals("0"))
            bindingObj.hideProfileSwitch.isChecked = true

        // This condition will only work if this activity is called from edit profile. It will only show and handle security question
        if(intent.hasExtra("fromEditProfile"))
        {
            if(intent.extras?.getBoolean("fromEditProfile")!!)
            {
                bindingObj.mainLayout.visibility = View.GONE
                bindingObj.securityQuestionTitleTxt.visibility = View.GONE
                bindingObj.titleLayout.titletxt.text = "Security Question"
            }
        }

        else
        {
            bindingObj.securityQuestionTitleTxt.visibility = View.GONE
            bindingObj.securityQuestionLayout.visibility = View.GONE
        }

    }

    fun clickListeners()
    {

        bindingObj.changeLocationBtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.changeBtn.setOnClickListener(this)
        bindingObj.changePasswordBtn.setOnClickListener(this)
        bindingObj.copyBtn.setOnClickListener(this)
        bindingObj.viewAnswerBtn.setOnClickListener(this)
        bindingObj.setSecurityQuestionBtn.setOnClickListener(this)
        bindingObj.updateRolesBtn.setOnClickListener(this)
        bindingObj.notificationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
                sharedPreferences.saveBoolean(Constants.isShowNotifications , true)

            else
                sharedPreferences.saveBoolean(Constants.isShowNotifications , false)

        }

        bindingObj.hideProfileSwitch.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                val mDialog = CustomDialog(this@SettingsActivity)
                mDialog.setTitle("Alert!")
                mDialog.setMessage("Do you really want to hide your profile?")
                mDialog.setPositiveButton("Yes" , DialogListenerInterface.onPositiveClickListener {

                    mPresenter.publisOrUnpublishProfile(sharedPreferences.getString(Constants.User_id).toString(), sharedPreferences.getString(Constants.User_Role).toString(), "0")
                    mDialog.dismiss() })

                mDialog.setNegativeButton("No" , DialogListenerInterface.onNegetiveClickListener {
                    bindingObj.hideProfileSwitch.isChecked =false
                    mDialog.dismiss()
                })

                mDialog.show()
            }

            else
            {
                mPresenter.publisOrUnpublishProfile(sharedPreferences.getString(Constants.User_id).toString() ,sharedPreferences.getString(Constants.User_Role).toString() ,"1" )
            }
        }
    }


    private fun apiCalling(user_id: String , user_role:String)
    {

        if(isNetworkActive()!!)
        {
            Timber.e(user_id+" "+user_role)
            mPresenter?.getProfileSettings(user_id , user_role)
        }
        else
        {

            val dialog = CustomDialog(this)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))

            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(user_id ,user_role)})

                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOCATIONREQUESTCODE)
        {
            bindingObj.locationTxt.text  = sharedPreferences.getString(Constants.CurrentAddress)
        }
    }

    override fun onGetProfile(username: String, url: String, answer: String, question_id: String, questions: MutableList<GetProfileSettingsPojo.QuestionsBean>, secondary_role_id: String, secondary_role: String) {

        this.primary_role = sharedPreferences.getString(Constants.User_Role).toString()
        this.seconday_role = secondary_role

        if(seconday_role.isEmpty())
            bindingObj.updateRolesBtn.visibility = View.GONE
        else
            bindingObj.updateRolesBtn.visibility = View.GONE


        bindingObj.profileRoleRecycler.layoutManager = GridLayoutManager(this, 2)
        bindingObj.profileRoleRecycler.adapter = ProfileRolesAdapter(this  ,mRoleList)

        bindingObj.profileLinkTxt.text = url
        if(username.length==0) {
            bindingObj.usernameEdittext.hint = "Username not set yet!"
            bindingObj.changeBtn.text = "Set"
        }
        else
        {
            bindingObj.changeBtn.text = "Reset"
            bindingObj.usernameEdittext.setText(username)
        }

        selectedAnswer = answer
        selectedQuestion_id = question_id

        Timber.e("Size "+ questions.size)
        for(i:Int in questions.indices)
        {
            mQuestionList.add(questions.get(i).question.toString())
            mQuestionIdsList.add(questions.get(i).question_id.toString())

            if (questions.get(i).question_id.toString().equals(selectedQuestion_id, ignoreCase = true))
            {
                selectedQuestion = questions.get(i).question.toString()
            }

        }


        if(!selectedQuestion_id.equals("null")&& !selectedQuestion_id.equals(""))
        {
            bindingObj.securityQuestionTxt.text = selectedQuestion.capitalize()
            bindingObj.setSecurityQuestionBtn.text = "Reset"
            bindingObj.viewAnswerBtn.visibility  = View.VISIBLE
        }
        else
        {
            bindingObj.securityQuestionTxt.text = "Set your security question"
            bindingObj.setSecurityQuestionBtn.text = "Set"
            bindingObj.viewAnswerBtn.visibility  = View.GONE
        }

    }

    override fun onUpdateSecondaryRole() {

        showSnackbar("Secondary role has been updated successfully")

        try
        {
            sharedPreferences.saveString(Constants.User_Secondary_Role, filter_category_list.get(0).toString())
        }

        catch (e:Exception)
        {
            e.printStackTrace()
        }

    }
    override fun onPublisOrUnpublish(status: String) {

        // Profile unpublished
        if(status.equals("1"))
        {
            sharedPreferences.saveString(Constants.IsProfile_published, "0")
        }
        // Profile published
        else
        {

            sharedPreferences.saveString(Constants.IsProfile_published, "1")
        }
    }

    override fun onUpdateUsername()
    {

        showSnackbar("Username has been updated successfully")
       // sharedPreferences.saveString(Constants.User_name , bindingObj.usernameEdittext.text.toString())

        bindingObj.usernameEdittext.hint = "Enter your username"
        bindingObj.changeBtn.text = "Change"

    }

    override fun onVerifiedPassword(status: Boolean, message: String) {

        if(aviDialogProgressBar!=null)
            aviDialogProgressBar.visibility= View.GONE

        if(status)
        {
            if(answerTxt!=null)
            {
                answerTxt.visibility = View.VISIBLE
                answerTxt.text = "Answer is: " + selectedAnswer
            }
        }

        else
        {
         Toast.makeText(this@SettingsActivity , message , Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSetSecurityQuestion(question: String , answer: String)
    {
        showSnackbar("Security question has been reset successfully")
        selectedQuestion = question
        selectedAnswer = answer

        bindingObj.securityQuestionTxt.text = selectedQuestion
        bindingObj.setSecurityQuestionBtn.text = "Reset"

    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun showProgress(visibility: Boolean)
    {

        if(visibility)
        {
            startProgressBarAnim()
        }
        else
            stopProgressBarAnim()
    }



    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }


    fun showSnackbar(message: String)
    {
        try
        {
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
            R.id.leftbtn ->
            {
/*                val intent = Intent()
                intent.putExtra("current_address", sharedPreferences.getString(Constants.CurrentAddress))
                setResult(100, intent)*/
                finish()
            }

            R.id.changeLocationBtn ->
            {
                startActivityForResult(Intent(this@SettingsActivity , LocationActivity::class.java ) ,LOCATIONREQUESTCODE)
            }

            R.id.copyBtn->
            {
                if(bindingObj.profileLinkTxt.text.toString().trim().length!=0)
                {
                    setClipboard(this@SettingsActivity , bindingObj.profileLinkTxt.text.toString())
                    showSnackbar("Text copied to clipboard")
                }
            }

            R.id.changeBtn->
            {

            validateUsernameDialog()
            }

            R.id.updateRolesBtn->
            {
                if(filter_category_list.size==0)
                    showSnackbar("Please select your role.")

                else
                {
                    if(isNetworkActiveWithMessage())
                    {
                        mPresenter.updateSecondaryRole(sharedPreferences.getString(Constants.User_id).toString() , filter_category_list.get(0).toString())
                    }

                }
            }
            R.id.changePasswordBtn->
            {
               startActivity(Intent(this@SettingsActivity , ChangePasswordActivity::class.java))
            }

            R.id.setSecurityQuestionBtn->
            {
              setSecurityQuestionDialog()
            }

            R.id.viewAnswerBtn->
            {
              showAnswerDialog()
            }
        }
    }

    fun showAnswerDialog()
    {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.show_answer_dialog_layout)
        val window = dialog.window
        val wlp = window!!.attributes

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val verifyPasswordBtn = dialog.findViewById<Button>(R.id.verifyPasswordBtn)
        val closeButton = dialog.findViewById<ImageView>(R.id.closeButton)
        val passwordEdittext = dialog.findViewById<EditText>(R.id.passwordEdittext)
        aviDialogProgressBar = dialog.findViewById<SpinKitView>(R.id.aviProgressBar)

        answerTxt = dialog.findViewById<TextView>(R.id.answerTxt)

        verifyPasswordBtn.setOnClickListener {

            if(passwordEdittext.text.toString().trim().length==0)
            {
                Toast.makeText(this@SettingsActivity ,"Please enter your password" , Toast.LENGTH_SHORT).show()
            }
            else
            {
                aviDialogProgressBar.visibility= View.VISIBLE
                mPresenter.verifyPassword(sharedPreferences.getString(Constants.User_id).toString(), passwordEdittext.text.toString().trim())
            }
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun setClipboard(context: Context, text: String)
    {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            var clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = text
        }
        else {

            val myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val myClip: ClipData? = ClipData.newPlainText("Copied Text", text)

            myClipboard?.setPrimaryClip(myClip!!)

        }
    }

    fun setSecurityQuestionDialog()
    {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.set_security_question_layout)
        val window = dialog.window
        val wlp = window!!.attributes

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        val submitBtn = dialog.findViewById<Button>(R.id.answerSubmitBtn)
        val closeButton = dialog.findViewById<ImageView>(R.id.closeButton)
        val answerEdittext = dialog.findViewById<EditText>(R.id.answerEdittext)
        val questionBtn = dialog.findViewById<RelativeLayout>(R.id.questionBtn)
        val questionTxt = dialog.findViewById<TextView>(R.id.questionTxt)

        var stateIndex =0

        for (i:Int in mQuestionIdsList.indices)
        {
            if(selectedAnswer.length!=0)
            {
                if (mQuestionIdsList.get(i).equals(selectedQuestion_id, ignoreCase = true)) {
                    stateIndex = i
                    questionTxt.text = mQuestionList[i]
                }
            }

        }

        questionBtn.setOnClickListener {

            val representerPicker = ItemPickerDialog(this@SettingsActivity, mQuestionList, stateIndex)
            representerPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
            {
                override fun onItemSelected(position: Int, selectedValue: String)
                {

                    selectedQuestion_id = mQuestionIdsList.get(position)
                    selectedQuestion =  mQuestionList.get(position)
                    Timber.e("Selected question " + selectedQuestion_id)
                    questionTxt.text = selectedQuestion
                    representerPicker.dismiss()
                }
            })

            representerPicker.show()
        }


        submitBtn.setOnClickListener {


            if(answerEdittext.text.toString().trim().length==0)
            {
                Toast.makeText(this@SettingsActivity ,"Please enter your answer" ,Toast.LENGTH_SHORT).show()
            }
            else if(selectedQuestion_id.length==0)
            {
                Toast.makeText(this@SettingsActivity ,"Please select your question" ,Toast.LENGTH_SHORT).show()
            }
            else
            {
                showPasswordDialog( answerEdittext.text.toString().trim(),selectedQuestion_id, selectedQuestion)
                dialog.dismiss()
            }


        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


    fun showPasswordDialog(answer:String , security_question_id : String , selected_question : String)
    {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.show_password_dialog_layout)
        val window = dialog.window
        val wlp = window!!.attributes

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val verifyPasswordBtn = dialog.findViewById<Button>(R.id.verifyPasswordBtn)
        val closeButton = dialog.findViewById<ImageView>(R.id.closeButton)
        val passwordEdittext = dialog.findViewById<EditText>(R.id.passwordEdittext)
       // aviDialogProgressBar = dialog.findViewById<SpinKitView>(R.id.aviProgressBar)

        verifyPasswordBtn.setOnClickListener {
            if(passwordEdittext.text.toString().trim().length==0)
            {
                Toast.makeText(this@SettingsActivity ,"Please enter your password" ,Toast.LENGTH_SHORT).show()
            }
            else
            {
                mPresenter.setSecurityQuestion(sharedPreferences.getString(Constants.User_id).toString(), security_question_id, answer, passwordEdittext.text.toString().trim(), selected_question)

                dialog.dismiss()
            }
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun populateCategories()
    {
        var map = HashMap<String , String>()
        map.put("id","2")
        map.put("role","Model")
        mRoleList.add(map)

        var map1 = HashMap<String , String>()
        map1.put("id","3")
        map1.put("role","Photographer")
        mRoleList.add(map1)

        var map2 = HashMap<String , String>()
        map2.put("id","4")
        map2.put("role","Makeup Artist")
        mRoleList.add(map2)

        var map3 = HashMap<String , String>()
        map3.put("id","5")
        map3.put("role","Stylist")
        mRoleList.add(map3)

        var map4 = HashMap<String , String>()
        map4.put("id","7")
        map4.put("role","Hair Stylist")
        mRoleList.add(map4)

        var map5 = HashMap<String , String>()
        map5.put("id","8")
        map5.put("role","Anchor")
        mRoleList.add(map5)

        var map6 = HashMap<String , String>()
        map6.put("id","9")
        map6.put("role","Dancer")
        mRoleList.add(map6)

        var map7 = HashMap<String , String>()
        map7.put("id","10")
        map7.put("role","Fashion Designer")
        mRoleList.add(map7)

        var map8 = HashMap<String , String>()
        map8.put("id","11")
        map8.put("role","Actor")
        mRoleList.add(map8)

        var map9 = HashMap<String , String>()
        map9.put("id","12")
        map9.put("role","Modelling Choreographer")
        mRoleList.add(map9)

        var map10 = HashMap<String , String>()
        map10.put("id","13")
        map10.put("role","Comedian")
        mRoleList.add(map10)

        var map11 = HashMap<String , String>()
        map11.put("id","14")
        map11.put("role","Singer")
        mRoleList.add(map11)
    }



    fun validateUsernameDialog()
    {
        isUsernameAvailable = false

        usernameDialog = Dialog(this)
        usernameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        usernameDialog.setContentView(R.layout.check_username_dialog_layout)
        val window = usernameDialog.window
        val wlp = window!!.attributes

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        usernameDialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        validateUsernameBtn = usernameDialog.findViewById<Button>(R.id.validateUsernameBtn)
        val closeButton = usernameDialog.findViewById<ImageView>(R.id.closeButton)
        val usernameEdittext = usernameDialog.findViewById<EditText>(R.id.usernameEdittext)
        usernameMessageTxt = usernameDialog.findViewById<TextView>(R.id.usernameMessageTxt)
        aviUsernameDialogProgressBar = usernameDialog.findViewById<SpinKitView>(R.id.aviProgressBar)

        usernameEdittext.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isUsernameAvailable = false
                validateUsernameBtn.text = "Validate Username"
                usernameMessageTxt.visibility = View.GONE
            }

        })
        validateUsernameBtn.setOnClickListener {

            if(usernameEdittext.text.isEmpty())
            {
                usernameMessageTxt.visibility = View.VISIBLE
                usernameMessageTxt.setTextColor(resources.getColor(R.color.appColor))
                usernameMessageTxt.text = "Enter your username!"
            }
            else {
                usernameMessageTxt.visibility = View.GONE
                if (isUsernameAvailable) {
                    if (isNetworkActiveWithMessage())
                        mUsernamePresenter.setUsername(sharedPreferences.getString(Constants.User_id).toString(), usernameEdittext.text.toString().trim())
                } else {
                    if (isNetworkActiveWithMessage())
                        mUsernamePresenter.checkUsernameAvailability(sharedPreferences.getString(Constants.User_id).toString(), usernameEdittext.text.toString().trim())

                }

            }

        }
        closeButton.setOnClickListener {
            usernameDialog.dismiss()
        }

        usernameDialog.show()

    }


    override fun onUsernameAvailable(username : String) {
        usernameMessageTxt.text = "Congrats! The username is available"
        usernameMessageTxt.visibility = View.VISIBLE
        usernameMessageTxt.setTextColor(resources.getColor(R.color.colorGreen))
        validateUsernameBtn.text = "Grab Now"
        isUsernameAvailable = true

    }

    override fun onSetUsername(username: String)
    {
        isUsernameAvailable = false
        usernameDialog.dismiss()
        showSnackbar("Username has been set successfully")
        bindingObj.usernameEdittext.setText(username)
    }

    override fun onUsernameFailed(message: String)
    {
        usernameMessageTxt.visibility = View.VISIBLE
        usernameMessageTxt.setTextColor(resources.getColor(R.color.appColor))
        usernameMessageTxt.text = "Username is not available. Try other"
    }

    override fun showUsernameDialogProgress(showProgress: Boolean)
    {
        if(showProgress)
            aviUsernameDialogProgressBar.visibility = View.VISIBLE

        else
            aviUsernameDialogProgressBar.visibility = View.GONE
    }
}
