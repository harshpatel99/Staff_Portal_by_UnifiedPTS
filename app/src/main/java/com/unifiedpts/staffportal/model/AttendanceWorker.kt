package com.unifiedpts.staffportal.model

data class AttendanceWorker(
    val uid: String?= null,
    var checkInTime: String? = null,
    var checkOutTime: String? = null,
    var date: String? = null,
    var totalWorkers:Int? = null,
    var totalOutsideWorkers:Int? = null,
    var workerUrl: String? = null,
    var engineerUrl: String? = null
)