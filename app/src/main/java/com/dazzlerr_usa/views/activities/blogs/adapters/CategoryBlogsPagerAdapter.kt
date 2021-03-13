package com.dazzlerr_usa.views.activities.blogs.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dazzlerr_usa.views.activities.blogs.fragments.AllBlogsFragment

import androidx.viewpager.widget.PagerAdapter
import android.view.ViewGroup
import androidx.fragment.app.FragmentStatePagerAdapter
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogCategoriesPojo


class CategoryBlogsPagerAdapter(fm: androidx.fragment.app.FragmentManager, var mCategoryList: MutableList<BlogCategoriesPojo.Result> , var TAG_ID:String ) : FragmentStatePagerAdapter(fm)
{
    //This will contain your Fragment references:
    var fragments = arrayOfNulls<Fragment>(mCategoryList.size)

    override fun getItem(position: Int): Fragment
    {

        val fragment = AllBlogsFragment()
        val bundle = Bundle()
        bundle.putString("cat_name",  mCategoryList.get(position).name)
        bundle.putString("cat_id",  mCategoryList.get(position).cat_id.toString())
        bundle.putString("tag_id",  TAG_ID)
        fragment.arguments = bundle

        return fragment
    }

    //This populates your Fragment reference array:
    override fun instantiateItem(container: ViewGroup, position: Int): Any
    {
        val createdFragment = super.instantiateItem(container, position) as Fragment
        fragments[position] = createdFragment
        return createdFragment
    }

    override fun getCount(): Int
    {
        return mCategoryList.size
    }

    override fun getPageTitle(position: Int): CharSequence?
    {
        return mCategoryList.get(position).name.toString()
    }

    override fun getItemPosition(obj: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}
