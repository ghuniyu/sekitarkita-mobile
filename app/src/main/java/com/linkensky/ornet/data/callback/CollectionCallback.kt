package com.linkensky.ornet.data.callback

import android.content.Context
import com.linkensky.ornet.data.response.BaseCollectionResponse

open class CollectionCallback<T>(context: Context) :
    DefaultCallback<BaseCollectionResponse<T>>(context)