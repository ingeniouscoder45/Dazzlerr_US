package com.dazzlerr_usa.utilities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityMediaPreviewBinding
import java.io.File

class MediaPreviewActivity : AppCompatActivity()  , View.OnClickListener
{
    lateinit var bindingObj : ActivityMediaPreviewBinding
    var uri = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_media_preview)

        initializations()
        clickListeners()

    }

    fun initializations()
    {
        uri = intent.extras?.getString("image_uri")!!

        Glide.with(this@MediaPreviewActivity)
                .load(File(uri))
                .placeholder(R.drawable.slider_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(bindingObj.ivPreview)
    }

    fun clickListeners()
    {
        bindingObj.cancelBtn.setOnClickListener(this)
        bindingObj.submitBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when(v?.id)
        {
            R.id.cancelBtn->
            {
                finish()
            }

            R.id.submitBtn->
            {
                val bundle = Bundle()
                bundle.putString("image_uri", uri)
                val intent = Intent()
                intent.putExtras(bundle)
                setResult(1001, intent)
                finish()
            }


        }
    }
}