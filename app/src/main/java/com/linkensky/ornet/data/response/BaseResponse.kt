package com.linkensky.ornet.data.response

open class BaseResponse(
    open val success: Boolean? = null,
    open val message: String? = null,
    open val errors: HashMap<String, List<String>>?
)