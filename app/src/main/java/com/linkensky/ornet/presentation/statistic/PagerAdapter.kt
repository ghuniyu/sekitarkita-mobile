package com.linkensky.ornet.presentation.statistic

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.linkensky.ornet.presentation.statistic.pages.CallCenterFragment
import com.linkensky.ornet.presentation.statistic.pages.HospitalFragment
import com.linkensky.ornet.presentation.statistic.pages.StatsTableFragment

class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    private val fragment = listOf(
        StatsTableFragment() to "Data Pasien",
        HospitalFragment() to "Rumah Sakit",
        CallCenterFragment() to "Call Center"
    )

    override fun getItem(position: Int): Fragment = fragment[position].first

    override fun getCount() = fragment.size

    override fun getPageTitle(position: Int): CharSequence? = fragment[position].second

}