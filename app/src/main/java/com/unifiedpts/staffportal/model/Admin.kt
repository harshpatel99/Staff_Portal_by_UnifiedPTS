package com.unifiedpts.staffportal.model

data class Admin(
    val email: String? = null,
    val password: String? = null,
    var recipientEmail: String? = null
) : java.io.Serializable