package com.example.hrapplication.ui.theme.screens

import com.example.hrapplication.data.RequestItem
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HRRequestsScreen(requests: List<RequestItem>, onDetailsClick: (RequestItem) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All status") }


    val filteredRequests = requests.filter { request ->
        val matchesFilter = when (selectedFilter) {
            "All status" -> true
            "Wait Dean" -> request.status == "Pending Dean Approval"
            "New" -> request.status == "On hold"
            else -> request.status.equals(selectedFilter, ignoreCase = true)
        }
        val matchesSearch = request.employeeName.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5F5))
            .padding(16.dp)
    ) {
        Text(
            "HR Dashboard",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8A282A)
        )

        Spacer(modifier = Modifier.height(20.dp))


        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search employees...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Menu, null, tint = Color.Gray) },
            trailingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            shape = RoundedCornerShape(25.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF8A282A)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))


        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(1.dp, Color.LightGray),
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
                modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)
            ) {
                // Dodali smo "Wait Dean" i "New" za lakšu navigaciju HR-u
                val options = listOf("All status", "New", "Wait Dean", "Approved", "Denied")
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

        Spacer(modifier = Modifier.height(24.dp))


        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9F9F9))
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Employee", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                    Text("Type", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                    Text("Start Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                    Text("Status", modifier = Modifier.weight(0.9f), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }

                HorizontalDivider(color = Color(0xFFEEEEEE))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredRequests) { request ->

                        val dates = request.date.split("-")
                        val startDate = dates.getOrNull(0)?.trim() ?: request.date

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDetailsClick(request) }
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(request.employeeName, modifier = Modifier.weight(1.2f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(request.type, modifier = Modifier.weight(0.8f), fontSize = 11.sp)
                            Text(startDate, modifier = Modifier.weight(1f), fontSize = 11.sp, color = Color.DarkGray)

                            Box(modifier = Modifier.weight(0.9f), contentAlignment = Alignment.Center) {
                                StatusChipSmall(request.status)
                            }
                        }
                        HorizontalDivider(color = Color(0xFFF9F9F9))
                    }
                }

                PaginationFooter()
            }
        }
    }
}

@Composable
fun PaginationFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier.clickable { /* Logika za nazad */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("←", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8A282A))
            Spacer(Modifier.width(4.dp))
            Text("Previous", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }


        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8A282A)),
                contentAlignment = Alignment.Center
            ) {
                Text("1", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Text("2", fontSize = 11.sp, color = Color.Gray)
            Text("3", fontSize = 11.sp, color = Color.Gray)
        }


        Row(
            modifier = Modifier.clickable { /* Logika za naprijed */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Next", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
            Text("→", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8A282A))
        }
    }
}

@Composable
fun StatusChipSmall(status: String) {

    val (color, label) = when (status) {
        "Approved" -> Color(0xFF437A54) to "Approved"
        "Denied", "Rejected" -> Color(0xFF8A282A) to "Denied"
        "Pending Dean Approval" -> Color(0xFF5D76A9) to "To Dean"
        "On hold" -> Color(0xFFD4C27F) to "New"
        else -> Color.Gray to status
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.width(75.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 4.dp),
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}