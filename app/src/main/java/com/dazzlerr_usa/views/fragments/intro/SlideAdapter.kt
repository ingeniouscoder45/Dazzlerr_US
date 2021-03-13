package com.dazzlerr_usa.views.fragments.intro

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.dazzlerr_usa.R

class SlideAdapter(internal var context: Context , var mFragment : Fragment) : androidx.viewpager.widget.PagerAdapter()
{
    internal lateinit var inflater: LayoutInflater
    public var mdots: Array<TextView?>? = null
    //Array
    var list_images = intArrayOf(

            R.drawable.intro_1, R.drawable.intro_2, R.drawable.intro_3, R.drawable.intro_4, R.drawable.intro_5, R.drawable.intro_6)


    override fun getCount(): Int {
        return list_images.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

       // (mFragment as IntroFragment).mCureentPage = position

        inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.intro, container, false)


        val img = view.findViewById<View>(R.id.slideimg) as ImageView
        val dots = view.findViewById<View>(R.id.dots) as LinearLayout
        val nextLayout = view.findViewById<View>(R.id.nextLayout) as LinearLayout
        val startLayout = view.findViewById<View>(R.id.startLayout) as RelativeLayout
        val nextBtn = view.findViewById<View>(R.id.nextBtn) as Button
        val skipBtn = view.findViewById<View>(R.id.skipBtn) as Button
        val startBtn = view.findViewById<View>(R.id.startBtn) as Button

        adddots(position , dots)

        img.setImageResource(list_images[position])
       // txt1.text = list_title[position]
       // txt2.text = list_description[position]


        if (position == 0)
        {
            nextBtn.isEnabled = true
            skipBtn.isEnabled = true
            skipBtn.visibility = View.VISIBLE

            nextBtn.text = "NEXT"
            skipBtn.text = "SKIP"
        }

        else if (position == mdots!!.size - 1)
        {

            nextBtn.isEnabled = true
            skipBtn.isEnabled = true
            skipBtn.visibility = View.VISIBLE

            nextBtn.text = "FINISH"
            skipBtn.text = "SKIP"
            nextLayout.visibility = View.GONE
            startBtn.visibility = View.VISIBLE
            startLayout.visibility = View.VISIBLE

        }
        else
        {
            nextBtn.isEnabled = true
            skipBtn.isEnabled = true
            skipBtn.visibility = View.VISIBLE

            nextBtn.text = "NEXT"
            skipBtn.text = "SKIP"
            nextLayout.visibility = View.VISIBLE
            startBtn.visibility = View.GONE
            startLayout.visibility = View.GONE
        }

        skipBtn.setOnClickListener {

            (mFragment as IntroFragment).skipSlide()
        }

        nextBtn.setOnClickListener {
            (mFragment as IntroFragment).nextSlide()
        }

        startBtn.setOnClickListener {
            (mFragment as IntroFragment).skipSlide()
        }
        container.addView(view)

        return view
    }

    fun adddots(i: Int, dots : LinearLayout)
    {

        mdots = arrayOfNulls(list_images.size)
        dots.removeAllViews()

        for (j in mdots!!.indices)
        {
            mdots!![j] = TextView(context)
            mdots!![j]?.text = Html.fromHtml("&#8226;")
            mdots!![j]?.textSize = 35f
            mdots!![j]?.setTextColor(ContextCompat.getColor(context!!, R.color.colorDarkGrey))

            dots.addView(mdots!![j])
        }

        if (mdots!!.size > 0)
        {
            mdots!![i]?.setTextColor(ContextCompat.getColor(context!! ,R.color.appColor))
        }

    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}
