package com.example.gmails4


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionsPagerAdapter(
    fm: FragmentManager,
    private val labeltitle:MutableList<LabelData>):FragmentPagerAdapter(fm){

    override fun getItem (position:Int): Fragment{ // now fragment
        return PlaceholderFragment.newInstance(position)// + 1 is position start 0
    }
    override fun getPageTitle(position: Int):CharSequence?{// now page title
        return labeltitle.get(position).name
    }
    override fun getCount(): Int {
        return labeltitle.size
    }
}