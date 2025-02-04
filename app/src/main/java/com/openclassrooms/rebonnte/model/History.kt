package com.openclassrooms.rebonnte.model
import java.util.Date

data class History(
    var medicineName: String = "",
    var userEmail: String = "",
    var date: Date = Date(),
    var details: String = ""
) {
    constructor() : this("", "", Date(), "")
}