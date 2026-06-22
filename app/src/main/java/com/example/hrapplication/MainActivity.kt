package com.example.hrapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hrapplication.data.RequestItem
import com.example.hrapplication.data.UserSession
import com.example.hrapplication.ui.theme.screens.*
import com.example.hrapplication.ui.theme.viewmodel.RequestViewModel
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val requestViewModel: RequestViewModel = viewModel()
                val requests by requestViewModel.requestsState

                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF9F0F0)) {
                    NavHost(navController = navController, startDestination = "login") {


                        composable("login") {
                            LoginScreen(onRoleSelected = { role ->
                                when (role) {
                                    "HR" -> navController.navigate("hr_list")
                                    "DEAN" -> navController.navigate("dean_list")
                                    else -> navController.navigate("main_list")
                                }
                            })
                        }


                        composable("main_list") {
                            LaunchedEffect(Unit) {
                                // Uzimamo najsvježijeg korisnika iz sesije tek kad uđemo na ekran
                                val user = UserSession.currentUser
                                requestViewModel.initRequests(
                                    user?.role ?: "EMPLOYEE",
                                    user?.id ?: "",
                                    user?.name ?: ""
                                )
                            }
                            RequestsScreen(
                                requests = requests,
                                onElementClick = { navController.navigate("details/${it.id}") },
                                onNewRequestClick = { navController.navigate("new_request") },
                                requestViewModel = requestViewModel
                            )
                        }


                        composable(
                            "details/{requestId}",
                            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("requestId") ?: ""
                            requests.find { it.id == id }?.let {
                                DetailsScreen(
                                    it.type,
                                    it.status,
                                    it.date,
                                    it.note,
                                    it.hrComment,
                                    it.deanComment,
                                    onCancelRequest = {
                                        requestViewModel.cancelRequest(it)
                                        navController.popBackStack()
                                    }
                                ) {
                                    navController.popBackStack()
                                }
                            }
                        }


                        composable("new_request") {
                            NewRequestScreen(
                                onBack = { navController.popBackStack() },
                                onSubmit = { type, dateRange, note ->
                                    val currentUser = UserSession.currentUser
                                    if (currentUser != null) {
                                        // SVI zahtjevi idu prvo HR-u na "On hold"
                                        val newRequest = RequestItem(
                                            id = UUID.randomUUID().toString(),
                                            employeeId = currentUser.id,
                                            employeeName = currentUser.name,
                                            type = type,
                                            date = dateRange,
                                            note = note,
                                            status = "On hold",
                                            assignedDeanId = ""
                                        )
                                        requestViewModel.addRequest(newRequest)
                                        navController.popBackStack()
                                    } else {
                                        println("Greška: Korisnik nije ulogovan u trenutku slanja!")
                                    }
                                },
                                requestViewModel = requestViewModel
                            )
                        }


                        composable("hr_list") {
                            LaunchedEffect(Unit) {
                                val user = UserSession.currentUser
                                requestViewModel.initRequests("HR", user?.id ?: "", user?.name ?: "")
                            }
                            HRRequestsScreen(
                                requests = requests,
                                onDetailsClick = { navController.navigate("hr_details/${it.id}") }
                            )
                        }


                        composable(
                            "hr_details/{requestId}",
                            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("requestId") ?: ""
                            

                            LaunchedEffect(id) {
                                requestViewModel.fetchRequestDetails(id)
                            }

                            val request by requestViewModel.currentRequestDetail
                            val employeeDetails by requestViewModel.selectedEmployeeDetails

                            request?.let {
                                HRDetailsScreen(
                                    request = it,
                                    employee = employeeDetails,
                                    onProcess = { comment, status ->
                                        requestViewModel.updateRequestStatus(it, comment, status, "HR")
                                        navController.popBackStack()
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }


                        composable("dean_list") {
                            LaunchedEffect(Unit) {
                                // Uzimamo ID dekana (npr. user_dean) iz sesije tek kad ekran krene
                                val user = UserSession.currentUser
                                requestViewModel.initRequests("DEAN", user?.id ?: "", user?.name ?: "")
                            }
                            DeanRequestsScreen(
                                requests = requests,
                                onDetailsClick = { navController.navigate("dean_details/${it.id}") }
                            )
                        }


                        composable(
                            "dean_details/{requestId}",
                            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("requestId") ?: ""
                            val request = requests.find { it.id == id }
                            request?.let {
                                DeanDetailsScreen(
                                    request = it,
                                    onFinalProcess = { comment, status ->
                                        requestViewModel.updateRequestStatus(it, comment, status, "DEAN")
                                        navController.popBackStack()
                                    },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}