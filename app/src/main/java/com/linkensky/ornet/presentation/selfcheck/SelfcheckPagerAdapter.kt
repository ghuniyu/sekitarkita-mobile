package com.linkensky.ornet.presentation.selfcheck

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.linkensky.ornet.presentation.selfcheck.questions.*

class SelfcheckPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val fragment = listOf(
        SelfcheckData(),
        Selfcheck1(),
        Selfcheck2(),
        Selfcheck3(),
        Selfcheck4(),
        Selfcheck5()
    )

    override fun getItemCount() = fragment.size

    override fun createFragment(position: Int) = fragment[position]
}