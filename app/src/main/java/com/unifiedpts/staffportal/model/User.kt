package com.unifiedpts.staffportal.model

data class User(
    var uid:String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val empID: String? = null,
    val userType: String? = null,
    val verifiedUser: String? = null,
    val createdOn: Long? = null
)