package com.linkensky.ornet.data

data class Province(
    val attributes: Attributes
)

data class Country(
    val meninggal: String,
    val name: String,
    val positif: String,
    val sembuh: String
)

data class Attributes(
    val FID: Int,
    val Kasus_Meni: Int,
    val Kasus_Posi: Int,
    val Kasus_Semb: Int,
    val Kode_Provi: Int,
    val Provinsi: String
)