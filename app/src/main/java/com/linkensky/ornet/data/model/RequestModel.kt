package com.linkensky.ornet.data.model

data class RequestReportData(
    val device_id: String,
    val has_fever: Boolean,
    val has_flu: Boolean,
    val has_cough: Boolean,
    val has_breath_problem: Boolean,
    val has_sore_throat: Boolean,
    val has_in_infected_country: Boolean,
    val has_in_infected_city: Boolean,
    val has_direct_contact: Boolean,
    val result: String,
    val name: String,
    val phone: String
)

data class InteractionHistoryRequest(
    val device_id: String
)