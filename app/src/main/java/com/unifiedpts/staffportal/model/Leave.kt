package com.unifiedpts.staffportal.model

data class Leave(
    val uid: String?= null,
    var appliedDate: Long? = null,
    var lastReminded: Long? = null,
    var fromDate: String? = null,
    var toDate: String? = null,
    var leaveType: String? = null,
    var reason: String? = null,
    var attachmentUrl: String? = null,
    var status: String? = null
) {
    companion object {
        val STATUS_APPROVED = "Approved"
        val STATUS_DENIED = "Denied"
        val STATUS_PENDING = "Pending"

        val APPROVED = 0
        val DENIED = 1
        val PENDING = 2

    }


}