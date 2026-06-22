package com.example.hrapplication.ui.theme.screens

import com.example.hrapplication.data.RequestItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeanRequestsScreen(
    requests: List<RequestItem>,
    onDetailsClick: (RequestItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAll by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F0F0))
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            "Dean Panel",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8A282A),
            modifier = Modifier.padding(bottom = 20.dp)
        )


        Surface(
            shape = RoundedCornerShape(30.dp),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by employee name...", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Menu, null, tint = Color.Gray) },
                trailingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )
        }


        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val filteredList = requests.filter { item ->
                val matchesSearch = item.employeeName.contains(searchQuery, ignoreCase = true)

                if (showAll) {
                    matchesSearch
                } else {

                    matchesSearch && (item.status == "Pending Dean Approval")
                }
            }

            items(filteredList) { item ->
                RequestCard(item = item, onClick = { onDetailsClick(item) })
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Show History (All)",
                fontSize = 18.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            Switch(
                checked = showAll,
                onCheckedChange = { showAll = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF333333),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}

@Composable
fun RequestCard(item: RequestItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = item.employeeName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF333333)
                )
                Text(
                    text = item.type,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = item.date,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }


            val (backgroundColor, statusLabel) = when (item.status) {
                "Approved" -> Color(0xFF437A54) to "Approved"
                "Denied", "Rejected" -> Color(0xFF8A282A) to "Denied"
                "Pending Dean Approval" -> Color(0xFFCBB15F) to "Wait Dean"
                else -> Color(0xFF757575) to item.status
            }

            Surface(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = statusLabel,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}