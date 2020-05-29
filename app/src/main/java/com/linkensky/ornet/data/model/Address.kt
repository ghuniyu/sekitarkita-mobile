package com.linkensky.ornet.data.model

data class Address(
    val village: String?,
    val district: String?,
    val city: String,
    val province: String
) {
    override fun toString(): String {
        return "$village $district, $city, $province"
    }
}