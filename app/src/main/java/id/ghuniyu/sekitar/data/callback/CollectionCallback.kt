package id.ghuniyu.sekitar.data.callback

import android.content.Context
import id.ghuniyu.sekitar.data.response.BaseCollectionResponse

open class CollectionCallback<T>(context: Context) :
    DefaultCallback<BaseCollectionResponse<T>>(context)