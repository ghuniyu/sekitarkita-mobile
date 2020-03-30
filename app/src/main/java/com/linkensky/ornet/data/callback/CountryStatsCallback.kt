package com.linkensky.ornet.data.callback

import android.content.Context
import com.linkensky.ornet.data.model.Country

open class CountryStatsCallback(context: Context) :
    DefaultCallback<List<Country>>(context)