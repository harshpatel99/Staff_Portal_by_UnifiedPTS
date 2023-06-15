package com.unifiedpts.staffportal.model

data class User(
    var uid: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    val phoneNumber: String? = null,
    val empID: String? = null,
    val userType: String? = null,
    val verifiedUser: String? = null,
    val createdOn: Long? = null,
    var expenses: Double? = null,
    val gratuity: Double? = null,
    val bonus: Double? = null,
    val loan: Double? = null,
    val sickLeave: Int? = null,
    val casualLeave: Int? = null,
    val privilegeLeave: Int? = null,
    val blLeave: Int? = null,
    val superiorEmail: String? = null,
    val appointmentLetterURL: String? = null,
    val incrementLetterURL: String? = null,
    val transferLetterURL: String? = null,
    val lastReminderDate: Long? = null
)