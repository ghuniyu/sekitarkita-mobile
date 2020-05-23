package com.linkensky.ornet.presentation.information.pages

import com.linkensky.ornet.itemCallCenter
import com.linkensky.ornet.presentation.base.BaseController
import com.linkensky.ornet.searchBar

class CallCenterController : BaseController() {
    override fun buildModels() {
        searchBar {
            id("search-bar")
            hint("Kota / Provinsi")
        }

        1.rangeTo(100).forEach {i ->
            itemCallCenter {
                id("cs-$i")
                province("Provinsi Gorontalo")
                area("Gorontalo")
            }
        }
    }
}