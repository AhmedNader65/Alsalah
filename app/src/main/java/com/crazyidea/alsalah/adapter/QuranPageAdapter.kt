package com.crazyidea.alsalah.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.crazyidea.alsalah.ui.quran.QuranPageFragment

class QuranPageAdapter(fm: Fragment) : FragmentStateAdapter(fm) {

    override fun getItemCount(): Int = 604

    override fun createFragment(position: Int): Fragment {
        return QuranPageFragment.newInstance(position.plus(1))
    }

}