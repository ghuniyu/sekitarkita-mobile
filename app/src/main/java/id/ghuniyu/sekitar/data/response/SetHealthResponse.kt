package id.ghuniyu.sekitar.data.response

import id.ghuniyu.sekitar.data.model.Device

data class SetHealthResponse(
    val device: Device
) : BaseResponse()