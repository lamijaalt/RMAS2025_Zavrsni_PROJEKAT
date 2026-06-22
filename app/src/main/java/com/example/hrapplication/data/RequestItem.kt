package com.example.hrapplication.data

data class RequestItem(
    val id: String = "",
    val employeeId: String = "",
    val employeeName: String = "Zaposlenik",
    val type: String = "",
    val date: String = "",
    val status: String = "On hold",
    val note: String = "",
    val hrComment: String = "",
    val deanComment: String = "",
    val assignedDeanId: String = "",
)
