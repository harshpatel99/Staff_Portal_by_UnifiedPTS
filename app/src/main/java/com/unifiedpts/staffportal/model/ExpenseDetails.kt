package com.unifiedpts.staffportal.model

data class ExpenseDetails(
    val uid: String,
    val date: Long,
    var state: String? = null,
    var city: String? = null,
    var projectName: String? = null,
    var projectNumber: String? = null,
    var floor: String? = null,
    var pour: String? = null,
    var work: String? = null,
    var remark: String? = null,
    var cashWorker: Double? = null,
    var workerAuto: Double? = null,
    var workerFood: Double? = null,
    var workerHotel: Double? = null,
    var engineerFood: Double? = null,
    var engineerAutoCab: Double? = null,
    var engineerHotel: Double? = null,
    var busTrainFare: Double? = null,
    var fuel: Double? = null,
    var materialTransportation: Double? = null,
    var printingStationary: Double? = null,
    var otherExpenses: Double? = null,
    var isDocAttached: Boolean? = null,
    var attachmentUrls: HashMap<String, String>? = null,
    var totalSpent: Double? = null
) : java.io.Serializable