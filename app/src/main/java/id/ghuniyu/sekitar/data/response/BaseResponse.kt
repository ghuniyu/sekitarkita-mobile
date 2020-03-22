package id.ghuniyu.sekitar.data.response

open class BaseResponse(
    open val success: Boolean? = null,
    open val message: String? = null,
    open val stack_trace: String? = null
)