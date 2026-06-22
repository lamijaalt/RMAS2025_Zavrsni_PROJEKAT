package com.example.hrapplication.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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

@Composable
fun RequestsScreen(
    requests: List<RequestItem>,
    onElementClick: (RequestItem) -> Unit,
    onNewRequestClick: () -> Unit,
    requestViewModel: RequestViewModel = viewModel()
) {

    val user by requestViewModel.currentUserState


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


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(requests) { request ->
                    RequestRow(request, onClick = { onElementClick(request) })
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