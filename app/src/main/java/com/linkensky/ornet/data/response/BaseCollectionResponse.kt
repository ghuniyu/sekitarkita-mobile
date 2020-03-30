package com.linkensky.ornet.data.response

data class BaseCollectionResponse<T>(
    val data: T
): BaseResponse()