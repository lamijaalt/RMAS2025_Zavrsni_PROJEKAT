package com.example.hrapplication.data

import retrofit2.http.GET
import retrofit2.http.Query


interface GoogleCalendarApi {
    @GET("v3/calendars/en.ba#holiday@group.v.calendar.google.com/events")
    suspend fun getHolidays(
        @Query("key") apiKey: String
    ): HolidayResponse
}


data class HolidayResponse(
    val items: List<HolidayItem> = emptyList()
)

data class HolidayItem(
    val start: HolidayDate? = null,
    val summary: String = ""
)

data class HolidayDate(
    val date: String? = null
)