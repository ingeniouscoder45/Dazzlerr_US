package com.dazzlerr_usa.views.activities.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityMessagesBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.messages.MessagesFragment
import javax.inject.Inject

class MessagesActivity : AppCompatActivity()
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObject: ActivityMessagesBinding
    internal lateinit var fragment_manager: androidx.fragment.app.FragmentManager
    internal lateinit var fragment_transaction: androidx.fragment.app.FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
      bindingObject =   DataBindingUtil.setContentView(this , R.layout.activity_messages)
        initializations()
        fragmentCalling()

    }

    fun loadFirstFragment(fragment: Fragment)
    {
        fragment_manager = supportFragmentManager
        fragment_transaction = fragment_manager.beginTransaction()
        fragment_transaction.replace(R.id.container, fragment)
        fragment_transaction.commitAllowingStateLoss()
    }

    fun fragmentCalling()
    {
        val fragment = MessagesFragment()
        //   fragment.arguments = bundle
        loadFirstFragment(fragment)
    }

    private fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)

    }



    fun startProgressBarAnim() {

        bindingObject.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObject.aviProgressBar.setVisibility(View.GONE)
    }

    fun isUserLogined():Boolean
    {
        if(sharedPreferences.getString(Constants.User_id).equals(""))
        {

            val dialog  = CustomDialog(this)
            dialog.setMessage(resources.getString(R.string.signin_txt))
            dialog.setPositiveButton("Continue", DialogListenerInterface.onPositiveClickListener
            {

                val bundle = Bundle()
                bundle.putString("ShouldOpenLogin"  , "true")
                val newIntent = Intent(this@MessagesActivity, MainActivity::class.java)
                newIntent.putExtras(bundle)
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(newIntent)
            })

            dialog.setNegativeButton("Cancel",  DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss() }
            )
            dialog.show()

            return false
        }

        else
        {
            return true
        }
    }

    override fun onResume()
    {
        super.onResume()

        Constants.IN_CHATLIST_SCREEN = true
    }

    override fun onPause()
    {
        super.onPause()
        Constants.IN_CHATLIST_SCREEN = false
    }

}
