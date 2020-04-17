package com.linkensky.ornet.data.response

data class DeviceResponse(
    val device_name: String,
    val firebase_token: String,
    val health_condition: String,
    val id: String,
    val label: String,
    val last_known_area: String,
    val last_known_latitude: String,
    val last_known_longitude: String,
    val online: Boolean,
    val phone: String
)