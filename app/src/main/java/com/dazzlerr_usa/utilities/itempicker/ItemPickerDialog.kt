package com.dazzlerr_usa.utilities.itempicker

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.dazzlerr_usa.R
import com.dpizarro.uipicker.library.picker.PickerUI
import com.dpizarro.uipicker.library.picker.PickerUISettings

public class ItemPickerDialog(mContext : Context , options: ArrayList<String> ,mPosition:Int )
{
    private var currentPosition = 0
    var dialog:Dialog
    var selectedResult = ""
    var doneBtn : TextView

    init {

        currentPosition = mPosition
        dialog = Dialog(mContext , R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView((mContext as Activity).layoutInflater.inflate(R.layout.item_picker_dialog_layout , null))

        val window: Window = dialog.window!!
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val wlp = window.attributes
        wlp.gravity = Gravity.BOTTOM
        // wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp

        val mPickerUI = dialog.findViewById<View>(R.id.picker_ui_view) as PickerUI
        doneBtn = dialog.findViewById<View>(R.id.doneBtn) as TextView

        //Populate list
        mPickerUI.setItems(mContext, options)
        mPickerUI.setColorTextCenter(R.color.colorBlack)
        mPickerUI.setColorTextNoCenter(R.color.colorLightGrey)
        mPickerUI.setLinesColor(R.color.colorBlack)
        mPickerUI.setItemsClickables(true)
        mPickerUI.setAutoDismiss(false)

        mPickerUI.setOnClickItemPickerUIListener { which, position, valueResult ->
            currentPosition = position
            //Toast.makeText(mContext, valueResult, Toast.LENGTH_SHORT).show();
            selectedResult = valueResult
        }


        val pickerUISettings = PickerUISettings.Builder().withItems(options)
                .withAutoDismiss(false)
                .withItemsClickables(true)
                .withUseBlur(false)
                .build()

        mPickerUI.setSettings(pickerUISettings)

        if (currentPosition == 0)
        {
            var position = 0
            if (options != null) {
                position = options.size / 2
            }
            mPickerUI.slide(position)
            currentPosition= position
        } else {
            mPickerUI.slide(currentPosition)
        }


    }
    fun show()
    {
        if (dialog != null) dialog.show()
    }

    fun dismiss()
    {
        if (dialog != null) dialog.dismiss()
    }

    fun onItemSelectedListener(listener: ItemPickerListener.onItemSelectedListener)
    {
        doneBtn.setOnClickListener {

            listener.onItemSelected(currentPosition , selectedResult)


        }
    }
}