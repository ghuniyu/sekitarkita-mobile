package id.ghuniyu.sekitar.data.callback

import android.content.Context
import id.ghuniyu.sekitar.data.model.Country

open class CountryStatsCallback(context: Context) :
    DefaultCallback<List<Country>>(context)