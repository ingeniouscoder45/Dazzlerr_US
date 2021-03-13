package com.dazzlerr_usa.views.activities.notifications

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityOccasionalNotificationHandlerBinding

class OccasionalNotificationHandlerActivity : AppCompatActivity()
{

    lateinit var bindingObj:ActivityOccasionalNotificationHandlerBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_occasional_notification_handler)
        initializations()
    }

    @SuppressLint("DefaultLocale")
    private fun initializations()
    {

        if(intent?.extras?.containsKey("title")!!)
        {

            bindingObj.occasionalTitleTxt.text = intent.extras!!.getString("title")

            if(intent.extras!!.getString("title")?.toLowerCase()?.contains("birthday")!!)
            {
                bindingObj.occasionalImage.setImageResource(R.drawable.occasional_cake)
                bindingObj.groupEmojiContainer?.addEmoji(R.drawable.occasional_cake)
                bindingObj.groupEmojiContainer?.addEmoji(R.drawable.occasional_confetti)
                bindingObj.groupEmojiContainer?.addEmoji(R.drawable.occasional_balloons)
            }

            else
            {
                bindingObj.occasionalImage.setImageResource(R.drawable.occasional_confetti)
                bindingObj.groupEmojiContainer?.addEmoji(R.drawable.occasional_confetti)
                bindingObj.groupEmojiContainer?.addEmoji(R.drawable.occasional_balloons)
            }
        }

        bindingObj.occasionalImage.visibility = View.GONE
        val anim = AnimationUtils.loadAnimation(this, R.anim.sp_anim3)
        val anim1 = AnimationUtils.loadAnimation(this, R.anim.enter_from_bottom)
        bindingObj.occasionalTitleTxt.startAnimation(anim)
        bindingObj.occasionalImage.visibility = View.VISIBLE
        bindingObj.occasionalImage.startAnimation(anim)
        bindingObj.occasionalImage.startAnimation(anim1)

        anim.setAnimationListener(object: Animation.AnimationListener
        {
            override fun onAnimationStart(animation: Animation?)
            {

            }
            override fun onAnimationEnd(animation: Animation?)
            {

            }
            override fun onAnimationRepeat(animation: Animation?)
            {
            }
        })

        try
        {
        bindingObj.groupEmojiContainer.startDropping()
        }
        catch(e:Exception)
        {
            e.printStackTrace()
        }

    }

}
