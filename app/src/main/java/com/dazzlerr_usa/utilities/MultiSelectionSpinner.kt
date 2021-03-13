package com.dazzlerr_usa.utilities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import com.dazzlerr_usa.R
import timber.log.Timber
import java.util.Arrays
import java.util.LinkedList


class MultiSelectionSpinner : Spinner, DialogInterface.OnMultiChoiceClickListener
{
    private val TAG = MultiSelectionSpinner::class.java.simpleName
    var _items: Array<String>? = null
    var mSelection: BooleanArray? = null
    var mSelectionAtStart: BooleanArray? = null
  //  var selectedItemsAsString: String =""

    internal var simple_adapter: ArrayAdapter<String>
    private var listener: OnMultipleItemsSelectedListener? = null

    var alertDialog: AlertDialog? = null

    val selectedItemsAsString: String
        get() {
            val sb = StringBuilder()
            var foundOne = false

            for (i in _items!!.indices)
            {
                if (mSelection!![i]) {

                    if(_items!![i]!="-Select-")
                    {
                        if (foundOne) {
                            sb.append(", ")
                        }
                        foundOne = true
                        sb.append(_items!![i])
                    }
                }
            }
            return sb.toString()
        }

    constructor(context: Context) : super(context)
    {

        simple_adapter = ArrayAdapter(context,
                R.layout.spinner_text_layout)
        super.setAdapter(simple_adapter)

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        simple_adapter = ArrayAdapter(context,
                R.layout.spinner_text_layout)
        super.setAdapter(simple_adapter)
    }

    fun setListener(listener: OnMultipleItemsSelectedListener) {
        this.listener = listener
    }

    override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
        if (mSelection != null && which < mSelection!!.size) {

            Timber.e("Index "+ which)
            if(which==0)
            {
                setDeselection(which)
            }
            else
            {
                mSelection!![which] = isChecked
                simple_adapter.clear()
                simple_adapter.add(buildSelectedItemString())
            }

/*            if (getSelectedIndices().size >0 ) {
                mSelection!![which] = isChecked
                simple_adapter.clear()
                simple_adapter.add(buildSelectedItemString())
            } else
            {
                Toast.makeText(context, "Please select", Toast.LENGTH_SHORT).show()
                setDeselection(which)
            }*/
        } else {
            throw IllegalArgumentException(
                    "Argument 'which' is out of bounds.")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
        buildDialogue()
        return true
    }

    private fun buildDialogue()
    {
        if (alertDialog != null && alertDialog!!.isShowing)
            alertDialog!!.dismiss()

        val builder = AlertDialog.Builder(context)
       // builder.setTitle("Please select!!!")
        builder.setCancelable(false)
        builder.setMultiChoiceItems(_items, mSelection, this)

        builder.setPositiveButton("Submit") { dialog, which ->
            System.arraycopy(mSelection!!, 0, mSelectionAtStart, 0, mSelection!!.size)
            if (listener != null) {
                listener!!.selectedIndices(getSelectedIndices())
                listener!!.selectedStrings(getSelectedStrings())
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            simple_adapter.clear()
            simple_adapter.add(selectedItemsAsString)
            System.arraycopy(mSelectionAtStart!!, 0, mSelection, 0, mSelectionAtStart!!.size)
        }
        alertDialog = builder.create()
        alertDialog!!.show()
    }

    override fun setAdapter(adapter: SpinnerAdapter) {
        throw RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.")
    }

    fun setItems(items: Array<String>) {
        _items = items
        mSelection = BooleanArray(_items!!.size)
        mSelectionAtStart = BooleanArray(_items!!.size)
        simple_adapter.clear()
        simple_adapter.add(_items!![0])
        Arrays.fill(mSelection, false)
        mSelection!![0] = true
        mSelectionAtStart!![0] = true
    }

    fun setItems(items: List<String>) {
        _items = items.toTypedArray()
        mSelection = BooleanArray(_items!!.size)
        mSelectionAtStart = BooleanArray(_items!!.size)
        simple_adapter.clear()
        simple_adapter.add(_items!![0])
        Arrays.fill(mSelection, false)
        mSelection!![0] = true
    }

    fun setSelection(selection: Array<String>) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
            mSelectionAtStart?.set(i, false)
        }
        for (cell in selection) {
            for (j in _items!!.indices) {
                if (_items!![j] == cell) {
                    mSelection!![j] = true
                    mSelectionAtStart?.set(j, true)
                }
            }
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    fun setSelectionlist(selection: MutableList<String>) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
            mSelectionAtStart?.set(i, false)
        }
        for (sel in selection) {
            for (j in _items!!.indices) {
                if (_items!![j] == sel.trim()) {
                    mSelection!![j] = true
                    mSelectionAtStart?.set(j, true)
                    Timber.e("Matched  "+ _items!![j])
                }
            }
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    override fun setSelection(index: Int) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
            mSelectionAtStart?.set(i, false)
        }
        if (index >= 0 && index < mSelection!!.size) {
            mSelection!![index] = true
            mSelectionAtStart?.set(index, true)
        } else {
            throw IllegalArgumentException("Index " + index
                    + " is out of bounds.")
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    fun setDeselection(index: Int) {

        if (index >= 0 && index < mSelection!!.size) {
            mSelection!![index] = false
            mSelectionAtStart?.set(index, false)

        } else {
            throw IllegalArgumentException("Index " + index
                    + " is out of bounds.")
        }
        simple_adapter.clear()
        simple_adapter.notifyDataSetChanged()
        simple_adapter.add(buildSelectedItemString())

        buildDialogue()
    }

    fun setSelection(selectedIndices: IntArray) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
            mSelectionAtStart?.set(i, false)
        }
        for (index in selectedIndices) {
            if (index >= 0 && index < mSelection!!.size) {
                mSelection!![index] = true
                mSelectionAtStart?.set(index, true)
            } else {
                throw IllegalArgumentException("Index " + index
                        + " is out of bounds.")
            }
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    fun getSelectedStrings(): List<String> {
        val selection = LinkedList<String>()
        for (i in _items!!.indices) {
            if (mSelection!![i]) {
                selection.add(_items!![i])
            }
        }
        return selection
    }

    fun getSelectedIndices(): List<Int> {
        val selection = LinkedList<Int>()
        for (i in _items!!.indices) {
            if (mSelection!![i]) {
                selection.add(i)
            }
        }
        return selection
    }

    private fun buildSelectedItemString(): String {
        val sb = StringBuilder()
        var foundOne = false

        for (i in _items!!.indices) {

            if(i!=0) {
                if (mSelection!![i]) {
                    if (foundOne) {
                        sb.append(", ")
                    }
                    foundOne = true

                    sb.append(_items!![i])
                }
            }
        }
        return sb.toString()
    }



    interface OnMultipleItemsSelectedListener {
        fun selectedIndices(indices: List<Int>)

        fun selectedStrings(strings: List<String>)
    }
}
