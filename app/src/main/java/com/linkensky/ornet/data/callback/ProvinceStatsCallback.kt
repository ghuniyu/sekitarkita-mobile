package com.linkensky.ornet.data.callback

import android.content.Context
import com.linkensky.ornet.data.model.Province

open class ProvinceStatsCallback(context: Context) :
    DefaultCallback<List<Province>>(context)