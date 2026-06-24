package com.example.hrapplication.ui.theme.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrapplication.data.RequestItem
import com.example.hrapplication.data.RequestRepository
import com.example.hrapplication.data.UserItem
import com.example.hrapplication.data.UserSession
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RequestViewModel : ViewModel() {
    private val repository = RequestRepository()

    private val _requestsState = mutableStateOf<List<RequestItem>>(emptyList())
    val requestsState: State<List<RequestItem>> = _requestsState

    private val _selectedEmployeeDetails = mutableStateOf<UserItem?>(null)
    val selectedEmployeeDetails: State<UserItem?> = _selectedEmployeeDetails

    private val _currentRequestDetail = mutableStateOf<RequestItem?>(null)
    val currentRequestDetail: State<RequestItem?> = _currentRequestDetail

    fun fetchRequestDetails(requestId: String) {
        viewModelScope.launch {
            val request = repository.fetchRequestById(requestId)
            _currentRequestDetail.value = request
            // Odmah učitaj i podatke o radniku ako je zahtjev pronađen
            request?.employeeId?.let { fetchEmployeeDetails(it) }
        }
    }

    private val _currentUserState = mutableStateOf<UserItem?>(null)
    val currentUserState: State<UserItem?> = _currentUserState

    var holidayMillis = mutableStateListOf<Long>()

    init {
        loadHolidays()
    }

    fun loadHolidays() {
        viewModelScope.launch {
            // Tvoj ključ ostaje, ali dodajemo error handling
            val key = "AIzaSyBv8t_a_eop1hYIKxLsTDZiUU5JBR9XXfc"
            try {
                val holidays = repository.fetchHolidays(key)
                holidayMillis.clear()
                holidayMillis.addAll(holidays)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun initRequests(role: String, userId: String, userName: String) {
        // Kada se inicijalizuju zahtjevi, osiguravamo da se prati i stanje korisnika
        if (_currentUserState.value == null && userId.isNotEmpty()) {
            listenToUserData(userId)
        }
        
        viewModelScope.launch {
            try {
                repository.getRequestsFlow(role, userId, userName).collect {
                    _requestsState.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addRequest(request: RequestItem) {
        viewModelScope.launch {
            repository.createRequest(request)
            // Automatski oduzmi dane čim se zahtjev napravi
            val days = calculateWorkDays(request.date)
            if (days > 0) {
                repository.updateUserDays(request.employeeId, days, request.type)
            }
        }
    }

    fun cancelRequest(request: RequestItem) {
        viewModelScope.launch {
            // 1. Obriši iz baze
            repository.deleteRequest(request.id)
            
            // 2. Vrati dane korisniku ako zahtjev nije bio već odbijen
            // (Ako je bio odbijen, dani su mu se već vratili kroz updateStatus)
            if (request.status != "Rejected" && request.status != "Denied") {
                val days = calculateWorkDays(request.date)
                if (days > 0) {
                    repository.updateUserDays(request.employeeId, -days, request.type)
                }
            }
        }
    }

    // --- POPRAVLJENA FUNKCIJA ZA BROJANJE DANA ---
    private fun calculateWorkDays(dateRange: String): Int {
        return try {
            val dates = dateRange.split(" - ")
            if (dates.size < 2) return 0
            
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            val start = sdf.parse(dates[0].trim())
            val end = sdf.parse(dates[1].trim())

            if (start != null && end != null) {
                var workDays = 0
                val cal = Calendar.getInstance()
                cal.time = start
                while (!cal.time.after(end)) {
                    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                    if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                        workDays++
                    }
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                workDays
            } else 0
        } catch (e: Exception) {
            0
        }
    }

    // --- POPRAVLJENO AŽURIRANJE STATUSA ---
    fun updateRequestStatus(request: RequestItem, comment: String, newStatus: String, role: String) {
        viewModelScope.launch {
            repository.updateStatusWithComment(request.id, newStatus, comment, role)

            // Ako je zahtjev ODBIJEN, moramo vratiti dane korisniku (jer smo ih oduzeli na početku)
            if (newStatus == "Rejected" || newStatus == "Denied") {
                val days = calculateWorkDays(request.date)
                if (days > 0) {
                    repository.updateUserDays(request.employeeId, -days, request.type)
                }
            }
        }
    }

    fun fetchEmployeeDetails(employeeId: String) {
        viewModelScope.launch {
            _selectedEmployeeDetails.value = repository.fetchUserById(employeeId)
        }
    }

    fun listenToUserData(identifier: String) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val cleanId = identifier.trim().lowercase()
        
        db.collection("users").document(cleanId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val updatedUser = UserItem(
                        id = snapshot.id,
                        name = snapshot.getString("name") ?: "Korisnik",
                        role = snapshot.getString("role") ?: "EMPLOYEE",
                        position = snapshot.getString("position") ?: "",
                        faculty = snapshot.getString("faculty") ?: "",
                        remainingLeave = snapshot.getLong("remainingLeave") ?: 0L,
                        workFromHomeDays = snapshot.getLong("workFromHomeDays") ?: 0L
                    )
                    _currentUserState.value = updatedUser
                    UserSession.currentUser = updatedUser
                }
            }
    }

    fun performLogin(email: String, pass: String, onComplete: (String?) -> Unit) {
        viewModelScope.launch {
            // 1. Prvo radimo pravu Firebase Authentication prijavu
            val authSuccess = repository.loginWithEmailAndPassword(email, pass)
            
            if (authSuccess) {
                // 2. Ako je lozinka tačna, povlačimo podatke o roli iz Firestore-a
                val user = repository.getUserData(email)
                if (user != null) {
                    UserSession.currentUser = user
                    _currentUserState.value = user
                    listenToUserData(user.id)
                    onComplete(user.role)
                } else {
                    onComplete(null) // Korisnik je u Auth bazi, ali nema dokument u 'users'
                }
            } else {
                onComplete(null) // Pogrešan email ili lozinka
            }
        }
    }
    
    fun checkFutureRequest(dateRange: String): Boolean {
        return repository.isDateinFuture(dateRange)
    }
}
