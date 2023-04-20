package com.unifiedpts.staffportal.model

data class User(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val empID: String,
    val userType: String,
    val isVerified: Boolean,
    val createdOn: Long,
    val hireDate: Long
)