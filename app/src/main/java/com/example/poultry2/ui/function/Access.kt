package com.example.poultry2.ui.function

import com.example.poultry2.data.Data
import com.example.poultry2.ui.global.filter.Filter

object Access {

    var listUserType= mutableListOf(
        Data.UserType("MG","MANAGEMENT"),
        Data.UserType("AC","AREA CUSTODIAN"),
        Data.UserType("CU","CUSTODIAN"),
        Data.UserType("SU","SUPERVISOR"),
        Data.UserType("DS","DSP"),
        Data.UserType("AD","AD")
    )

    private var listPolicy= mutableListOf(
        Data.Policy("SIV DASH TAB",listOf("MG","AC","CU","SU")),
        Data.Policy("SAVE SIV TARGET",listOf("SU")),
        Data.Policy("DSP DASH TAB",listOf("MG","AC","CU","SU")),
        Data.Policy("SAVE DSP TARGET",listOf("CU","SU")),
        Data.Policy("SAVE ACCOUNT TARGET",listOf("CU","SU","DS")),
        Data.Policy("SAVE CUSTOMER TARGET",listOf("CU","SU")),
        Data.Policy("SAVE ACCOUNT GEO TAG",listOf("DS")),
        Data.Policy("SAVE ACCOUNT FREQ",listOf("CU","SU")),
        Data.Policy("SAVE VISIT",listOf("DS")),
        Data.Policy("SAVE ORDER",listOf("MG","AC","CU","SU","DS"))
    )

    fun granted(name:String):Boolean{
        val access=listPolicy.find { it.name==name}
        return (access!=null && access.userTypeCodeList.contains(Filter.user.userType))
    }


}