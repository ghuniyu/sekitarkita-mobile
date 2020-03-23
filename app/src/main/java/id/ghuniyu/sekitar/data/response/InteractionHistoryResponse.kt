package id.ghuniyu.sekitar.data.response

import id.ghuniyu.sekitar.data.model.Interaction

data class InteractionHistoryResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val nearbies: List<Interaction>
)