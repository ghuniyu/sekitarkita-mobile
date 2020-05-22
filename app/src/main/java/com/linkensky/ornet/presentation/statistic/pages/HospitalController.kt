package com.linkensky.ornet.presentation.statistic.pages

import com.linkensky.ornet.hospital
import com.linkensky.ornet.presentation.base.BaseController

class HospitalController : BaseController(){
    override fun buildModels() {
        1.rangeTo(100).forEach {i ->
            hospital {
                id("hospital-$i")
                name("Rumah sakit umum daerah dr.Zainoel Abidin")
                address("Jl. Teuku Mo.Daud Beureuh No.108, Bandar baru, Kec. Kuta Alam, Kota Banda Aceh, Aceh 24415")
                phone("06513465")
            }
        }
    }

}