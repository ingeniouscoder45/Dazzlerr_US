package com.dazzlerr_usa.views.activities.portfolio

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.loader.content.CursorLoader
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAddAudioBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class AddAudioActivity : AppCompatActivity() , View.OnClickListener
{
    lateinit var bindingObj : ActivityAddAudioBinding
    var uri_path = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_add_audio)
        initializations()
        clickListeners()
    }


    fun initializations()
    {
        bindingObj.titleLayout.titletxt.setText("Add Audio")
    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.audioSubmitBtn.setOnClickListener(this)
        bindingObj.addEditAudioBtn.setOnClickListener(this)
    }


    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn->
                finish()

            R.id.audioSubmitBtn->
            {

                if(bindingObj.audioTitleEdittext.text.toString().trim().isEmpty())
                {
                    showSnackbar("Please enter the title for your audio.")
                }
                else if(uri_path.equals("null") || uri_path.isEmpty())
                {
                    showSnackbar("Please pick a audio file.")
                }
                else
                {

                    if(isNetworkActiveWithMessage()) {
                        val bundle = Bundle()
                        bundle.putString("uri_path", uri_path)
                        bundle.putString("title", bindingObj.audioTitleEdittext.text.toString().trim())

                        val intent = Intent()
                        intent.putExtras(bundle)
                        setResult(1004, intent)
                        finish()

                    }
                }
            }

            R.id.addEditAudioBtn->
            {
                val audioIntent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(Intent.createChooser(audioIntent,"Select Audio"), 1001)
            }
        }
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


    private fun getAudioPath(uri: Uri): String? {
        val data = arrayOf(MediaStore.Audio.Media.DATA)
        val loader = CursorLoader(applicationContext!!, uri, data, null, null, null)
        val cursor: Cursor? = loader.loadInBackground()
        val columnindex: Int? = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor?.moveToFirst()
        return cursor?.getString(columnindex!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            1001 -> {

                if (resultCode == Activity.RESULT_OK)
                {

                    Timber.e("Path : "+ data?.data!!.path)

                    uri_path = getAudioPath(data?.data!!).toString()
                    bindingObj.addEditAudioBtn.text = "Edit audio"
                    bindingObj.audioUrl.text = uri_path?.substring(uri_path.toString().lastIndexOf("/")+1)
                }
            }
        }
    }
}