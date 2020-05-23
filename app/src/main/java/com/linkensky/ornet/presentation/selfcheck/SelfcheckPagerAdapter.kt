package com.linkensky.ornet.presentation.selfcheck

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.linkensky.ornet.presentation.information.pages.CallCenterFragment
import com.linkensky.ornet.presentation.information.pages.HospitalFragment
import com.linkensky.ornet.presentation.information.pages.StatsTableFragment

class SelfcheckPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    private val fragment = listOf(
        StatsTableFragment(),
        HospitalFragment(),
        CallCenterFragment()
    )

    override fun getItem(position: Int): Fragment = fragment[position]
    override fun getCount() = fragment.size
}