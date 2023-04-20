package com.unifiedpts.staffportal.model

data class ExpenseDetails(
    val date: Long,
    var state: String? = null,
    var city: String? = null,
    var projectName: String? = null,
    var projectNumber: String? = null,
    var floor: String? = null,
    var pour: String? = null,
    var work: String? = null,
    var remark: String? = null,
    var cashWorker: Float? = null,
    var workerAuto: Float? = null,
    var workerFood: Float? = null,
    var workerHotel: Float? = null,
    var engineerFood: Float? = null,
    var engineerAutoCab: Float? = null,
    var engineerHotel: Float? = null,
    var busTrainFare: Float? = null,
    var fuel: Float? = null,
    var materialTransportation: Float? = null,
    var printingStationary: Float? = null,
    var otherExpenses: Float? = null,
    var isDocAttached: Boolean? = null,
    var attachmentUrls: HashMap<String, String>? = null,
    var totalSpent: Float? = null
) : java.io.Serializable