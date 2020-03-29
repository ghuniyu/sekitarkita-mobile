package id.ghuniyu.sekitar.data.response

data class BaseCollectionResponse<T>(
    val data: T
): BaseResponse()