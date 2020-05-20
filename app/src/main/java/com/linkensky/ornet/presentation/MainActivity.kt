package com.linkensky.ornet.presentation

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.ActivityMainBinding
import com.linkensky.ornet.presentation.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val topDestination = listOf(
        R.id.homeFragment,
        R.id.interactionFragment,
        R.id.statisticFragment,
        R.id.reportFragment
    )

    override fun getLayoutRes() = R.layout.activity_main

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        binding.apply {
            val navController = findNavController(R.id.navHostFragment)
            NavigationUI.setupWithNavController(bottomNavigation, navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (topDestination.contains(destination.id)) {
                    bottomNavigation.visibility = View.VISIBLE
                } else {
                    bottomNavigation.visibility = View.GONE
                }
            }
        }
    }
}