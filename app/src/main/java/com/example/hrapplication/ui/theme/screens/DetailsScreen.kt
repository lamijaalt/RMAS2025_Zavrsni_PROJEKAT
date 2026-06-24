package com.example.hrapplication.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailsScreen(
    requestType: String,
    status: String,
    date: String,
    note: String,
    hrComment: String = "",
    deanComment: String = "",
    isFuture: Boolean,
    onCancelRequest: () -> Unit = {},
    onBack: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    val statusColor = when (status) {
        "Approved" -> Color(0xFF437A54)
        "Denied", "Rejected" -> Color(0xFF8A282A)
        "Pending Dean Approval", "On hold" -> Color(0xFFD4C27F)
        else -> Color(0xFFD4C27F)
    }

    val displayStatus = if (status == "Pending Dean Approval") "Pending" else status

    val statusDescription = when (status) {
        "Approved" -> "Everything looks good. Your request has been approved by the Dean."
        "Denied", "Rejected" -> "The request was denied due to overlapping schedules or administrative reasons."
        "Pending Dean Approval" -> "HR has reviewed your request. It is now with the Dean for final signature."
        else -> "Your request is currently being processed by the HR department."
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Request") },
            text = { Text("Are you sure you want to cancel this request? Your days will be refunded.") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    onCancelRequest()
                }) {
                    Text("Yes, Cancel", color = Color(0xFF8A282A), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5F5))
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Details",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8A282A))
                Text(
                    "Request Info",
                    color = Color.Gray,
                    fontSize = 14.sp)
            }
            Surface(
                color = statusColor,
                shape = RoundedCornerShape(50)) {
                Text(
                    text = displayStatus,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        InfoCard(title = "General Information") {
            DetailsDetailRow("Type", requestType)
            DetailsDetailRow("Period", date)
            DetailsDetailRow("Note", if (note.isEmpty()) "No additional notes" else note)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (hrComment.isNotEmpty() || deanComment.isNotEmpty()) {
            InfoCard(title = "Comments") {
                if (hrComment.isNotEmpty()) {
                    Text("HR Comment:", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(hrComment, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                }
                if (deanComment.isNotEmpty()) {
                    Text("Dean Comment:", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(deanComment, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        InfoCard(title = "Approval Process") {
            Text(
                "Current Status Update",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                color = Color.Gray)

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF9F9F9),
                border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = statusDescription,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 13.sp,
                    color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            DetailsDetailRow("Progress", if (status == "Approved" || status == "Denied" || status == "Rejected") "Completed" else "In Progress")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (status == "On hold" || status == "Pending Dean Approval" || (status == "Approved" && isFuture)) {
                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.weight(1f).height(50.dp),
                    border = BorderStroke(1.dp, Color(0xFFA66D6F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel Request", color = Color(0xFFA66D6F))
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF828282)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
private fun DetailsDetailRow(label: String, value: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color.Gray, fontSize = 14.sp)
            Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF444444))
        }
        HorizontalDivider(color = Color(0xFFF1F1F1), thickness = 1.dp)
    }
}
