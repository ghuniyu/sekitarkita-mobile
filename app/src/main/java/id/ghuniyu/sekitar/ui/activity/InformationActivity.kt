package id.ghuniyu.sekitar.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.ui.fragment.CallCenterFragment
import id.ghuniyu.sekitar.ui.fragment.HospitalFragment
import id.ghuniyu.sekitar.ui.fragment.StatisticFragment
import kotlinx.android.synthetic.main.activity_information.*

class InformationActivity : BaseActivity() {

    override fun getLayout() = R.layout.activity_information

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Informasi"
        toolbar.navigationIcon = R.drawable.ic_arrow_white.resDrawable()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        pager.offscreenPageLimit = 2
        pager.adapter = InfoPagerAdapter(supportFragmentManager)
        tab.setupWithViewPager(pager)
    }

    inner class InfoPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragment = listOf(
           StatisticFragment() to "Statistik",
           HospitalFragment() to "Rumah Sakit",
           CallCenterFragment() to "Call Center"
        )

        override fun getItem(position: Int): Fragment = fragment[position].first

        override fun getCount(): Int = fragment.size

        override fun getPageTitle(position: Int): CharSequence? = fragment[position].second
    }
}
