package com.example.hrapplication.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hrapplication.data.RequestItem
import com.example.hrapplication.data.UserSession
import com.example.hrapplication.ui.theme.viewmodel.RequestViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    requests: List<RequestItem>,
    onElementClick: (RequestItem) -> Unit,
    onNewRequestClick: () -> Unit,
    requestViewModel: RequestViewModel = viewModel()
) {

    val user by requestViewModel.currentUserState


    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All status") }


    val filteredRequests = requests.filter { request ->
        val matchesFilter = when (selectedFilter) {
            "All status" -> true
            "New" -> request.status == "On hold"
            "To Dean" -> request.status == "Pending Dean Approval"
            else -> request.status.equals(selectedFilter, ignoreCase = true)
        }
        val matchesSearch = request.type.contains(searchQuery, ignoreCase = true) ||
                request.date.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    LaunchedEffect(Unit) {
        val currentId = user?.id ?: UserSession.currentUser?.id
        if (!currentId.isNullOrEmpty()) {
            requestViewModel.listenToUserData(currentId)
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "My requests",
                color = Color(0xFF8A282A),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                placeholder = { Text("Search requests...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                shape = RoundedCornerShape(25.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF8A282A)
                )
            )

            Box {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { expanded = true },
                    shape = RoundedCornerShape(25.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FolderOpen, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text(selectedFilter, modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray)
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    val options = listOf("All status", "New", "To Dean", "Approved", "Denied")
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedFilter = option
                                expanded = false
                            }
                        )
                    }
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    title = "Remaining days :",
                    value = "${user?.remainingLeave ?: 0}",
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Remote work :",
                    value = "${user?.workFromHomeDays ?: 0} d",
                    modifier = Modifier.weight(1f)
                )
            }


            if (filteredRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No requests available",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredRequests) { request ->
                        RequestRow(request, onClick = { onElementClick(request) })
                    }
                }
            }
        }


        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(65.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF8A282A))
                .clickable { onNewRequestClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.EditNote,
                null,
                tint = Color.White,
                modifier = Modifier.size(35.dp)
            )
        }
    }
}

@Composable
fun RequestRow(request: RequestItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(45.dp).clip(CircleShape).background(Color(0xFF8A282A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if(request.type.isNotEmpty()) request.type.first().toString() else "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(request.type, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(request.date, color = Color.Gray, fontSize = 14.sp)
                    StatusChip(request.status)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text(title, fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (displayText, color) = when (status) {
        "Approved" -> "Approved" to Color(0xFF437A54)
        "On hold" -> "On hold" to Color(0xFFD4C27F)
        "Denied", "Rejected" -> "Rejected" to Color(0xFF8A282A)
        "Pending Dean Approval" -> "Pending" to Color(0xFF5D76A9)
        else -> status to Color.Gray
    }
    Surface(
        color = color,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = displayText,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 23.dp, vertical = 3.dp)
        )
    }
}