package com.linkensky.ornet.presentation.information.pages

import com.linkensky.ornet.presentation.base.BaseController
import com.linkensky.ornet.searchBar

class CallCenterController : BaseController() {
    override fun buildModels() {
        searchBar {
            id("search-bar")
            hint("Rumah sakit / Kota / Provinsi")
        }

    }
}