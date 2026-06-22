package com.example.hrapplication.data

data class UserItem(
    var id: String = "",
    var name: String = "",
    var role: String = "",
    var position: String = "",
    var faculty: String = "",
    var remainingLeave: Long = 0,
    var workFromHomeDays: Long = 0
)

object UserSession {
    var currentUser: UserItem? = null
}