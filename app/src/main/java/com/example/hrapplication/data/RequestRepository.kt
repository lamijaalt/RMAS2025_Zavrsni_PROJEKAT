package com.example.hrapplication.data

import com.example.hrapplication.data.RequestItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import com.example.hrapplication.data.GoogleCalendarApi

class RequestRepository {
    private val db = FirebaseFirestore.getInstance()
    private val requestsCollection = db.collection("requests")


    suspend fun fetchHolidays(apiKey: String): List<Long> {
        val calendarApi = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/calendar/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleCalendarApi::class.java)

        return try {
            val response = calendarApi.getHolidays(apiKey)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")


            response.items?.mapNotNull { item ->
                item.start?.date?.let { dateString ->
                    sdf.parse(dateString)?.time
                }
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    suspend fun updateUserDays(userEmail: String, daysToSubtract: Int, type: String) {
        try {

            val userRef = db.collection("users").document(userEmail)
            val userDoc = userRef.get().await()

            if (userDoc.exists()) {
                val fieldToUpdate = when (type) {
                    "Vacation" -> "remainingLeave"
                    "Remote Work" -> "workFromHomeDays"
                    else -> null
                }

                if (fieldToUpdate != null) {
                    val currentDays = userDoc.getLong(fieldToUpdate) ?: 0L
                    val newDays = (currentDays - daysToSubtract).coerceAtLeast(0)
                    userRef.update(fieldToUpdate, newDays).await()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun createRequest(request: RequestItem) {
        try {
            val newDocRef = requestsCollection.document()
            val requestWithId = request.copy(id = newDocRef.id)
            newDocRef.set(requestWithId).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteRequest(requestId: String) {
        try {
            requestsCollection.document(requestId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getRequestsFlow(role: String, userId: String, userName: String): Flow<List<RequestItem>> = callbackFlow {
        val query = when (role) {
            "EMPLOYEE" -> requestsCollection.whereEqualTo("employeeId", userId) // Koristimo employeeId!
            "HR" -> requestsCollection.orderBy("date", Query.Direction.DESCENDING)
            "DEAN" -> requestsCollection.whereEqualTo("assignedDeanId", userId)
            else -> requestsCollection
        }

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                trySend(emptyList())
                return@addSnapshotListener
            }
            snapshot?.let {
                val items = it.toObjects(RequestItem::class.java)
                trySend(items)
            }
        }
        awaitClose { subscription.remove() }
    }


    suspend fun fetchRequestById(requestId: String): RequestItem? {
        return try {
            val doc = requestsCollection.document(requestId).get().await()
            if (doc.exists()) doc.toObject(RequestItem::class.java) else null
        } catch (e: Exception) { null }
    }


    suspend fun updateStatusWithComment(requestId: String, newStatus: String, comment: String, role: String) {
        try {
            val updates = mutableMapOf<String, Any>("status" to newStatus)
            
            if (role == "HR") {
                updates["hrComment"] = comment
                if (newStatus == "Pending Dean Approval") {
                    updates["assignedDeanId"] = "dean@fet.ba"
                }
            } else if (role == "DEAN") {
                updates["deanComment"] = comment
            }

            db.collection("requests").document(requestId).update(updates).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun fetchUserById(userId: String): UserItem? {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            if (doc.exists()) {
                UserItem(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    role = doc.getString("role") ?: "",
                    position = doc.getString("position") ?: "",
                    faculty = doc.getString("faculty") ?: "",
                    remainingLeave = doc.getLong("remainingLeave") ?: 0L,
                    workFromHomeDays = doc.getLong("workFromHomeDays") ?: 0L
                )
            } else null
        } catch (e: Exception) { null }
    }


    private fun mapDocumentToUser(doc: com.google.firebase.firestore.DocumentSnapshot): UserItem {
        val rawName = doc.getString("name")
        return UserItem(
            id = doc.id,
            name = if (!rawName.isNullOrBlank()) rawName else doc.id.split("@")[0].replaceFirstChar { it.uppercase() },
            role = doc.getString("role") ?: "",
            position = doc.getString("position") ?: "",
            faculty = doc.getString("faculty") ?: "",
            remainingLeave = doc.getLong("remainingLeave") ?: 0L,
            workFromHomeDays = doc.getLong("workFromHomeDays") ?: 0L
        )
    }


    suspend fun getUserData(identifier: String): UserItem? {
        return try {
            val cleanId = identifier.trim().lowercase()
            val usersRef = db.collection("users")


            val doc = usersRef.document(cleanId).get().await()
            if (doc.exists()) return mapDocumentToUser(doc)
            

            if (!cleanId.contains("@")) {
                val docWithEmail = usersRef.document("$cleanId@fet.ba").get().await()
                if (docWithEmail.exists()) return mapDocumentToUser(docWithEmail)
            }


            val query = usersRef.whereIn("id", listOf(cleanId, "$cleanId@fet.ba")).get().await()
            if (!query.isEmpty) return mapDocumentToUser(query.documents[0])

            null
        } catch (e: Exception) {
            null
        }
    }

    // 8. Firebase Auth prijava
    suspend fun loginWithEmailAndPassword(email: String, password: String): Boolean {
        return try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {

            println("Login error: ${e.message}")
            false
        }
    }
}