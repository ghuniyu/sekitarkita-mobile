package com.linkensky.ornet.data.model

data class Address @JvmOverloads constructor(
    val village: String? = null,
    val district: String? = null,
    val city: String? = null,
    val province: String? = null
) {
    override fun toString(): String {
        return "$village $district, $city, $province"
    }

    fun location(): String {
        return if (village != null && district != null) "$village, $district" else "Lokasi tidak diketahui"
    }
}