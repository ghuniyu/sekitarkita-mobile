package com.linkensky.ornet.data.event

class PingEvent(
    val device_id: String,
    val latitude: Double,
    val longitude: Double,
    val area: String
)