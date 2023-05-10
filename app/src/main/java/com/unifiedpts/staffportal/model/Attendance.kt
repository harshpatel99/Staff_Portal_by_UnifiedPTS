package com.unifiedpts.staffportal.model

data class Attendance(
    val uid: String?= null,
    var checkInTime: String? = null,
    var checkOutTime: String? = null,
    var date: String? = null
)