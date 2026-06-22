package com.example.hrapplication.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hrapplication.data.RequestItem
import com.example.hrapplication.data.UserItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HRDetailsScreen(
    request: RequestItem,
    employee: UserItem?,
    onProcess: (String, String) -> Unit,
    onBack: () -> Unit
) {

    var hrComment by remember { mutableStateOf(request.hrComment) }


    val (statusText, statusColor) = when (request.status) {
        "On hold" -> "New Request" to Color(0xFFD4C27F)
        "Pending Dean Approval" -> "With Dean" to Color(0xFF5D76A9)
        "Approved" -> "Approved" to Color(0xFF437A54)
        "Denied", "Rejected" -> "Rejected" to Color(0xFF8A282A)
        else -> request.status to Color.Gray
    }


    val isProcessed = request.status != "On hold"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F0F0))
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF8A282A))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("HR Review", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8A282A))
                Text("ID: #${request.id.takeLast(6)}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))

            Surface(
                color = statusColor,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = statusText,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {


            InfoCard(
                title = "Employee Information",
                icon = { Icon(Icons.Default.PersonOutline, null, tint = Color(0xFF8A282A)) }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(employee?.name ?: request.employeeName, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(employee?.position ?: "Staff Member", color = Color(0xFF8A282A), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(employee?.faculty ?: "University Faculty", color = Color.Gray, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Employee Stats", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
                        Text("• Role: ${employee?.role ?: "N/A"}", fontSize = 12.sp)
                        Text("• Vacation: ${employee?.remainingLeave ?: 0} days left", fontSize = 12.sp)
                        Text("• Remote: ${employee?.workFromHomeDays ?: 0} days left", fontSize = 12.sp)
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF8A282A).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = request.employeeName.take(1),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8A282A)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color(0xFFEEEEEE))

                Text("Request Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                DetailRowMinimal("Type", request.type)
                DetailRowMinimal("Dates", request.date)
                DetailRowMinimal("Note", if(request.note.isEmpty()) "No note provided" else request.note)
            }

            Spacer(modifier = Modifier.height(16.dp))


            InfoCard(
                title = "Comment",
                icon = { Icon(Icons.Default.AccessTime, null, tint = Color(0xFF8A282A)) }
            ) {
                Text("Internal Comment (Visible to Dean)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = hrComment,
                    onValueChange = { hrComment = it },
                    placeholder = { Text("Why are you forwarding/rejecting this?") },
                    modifier = Modifier.fillMaxWidth().height(110.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8A282A),
                        unfocusedBorderColor = Color.LightGray,
                        disabledBorderColor = Color(0xFFEEEEEE)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            if (!isProcessed) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onProcess(hrComment, "Pending Dean Approval") },
                        modifier = Modifier.weight(1.2f).height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A282A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Forward to Dean", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onProcess(hrComment, "Rejected") },
                        modifier = Modifier.weight(0.8f).height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0), contentColor = Color.DarkGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reject", fontSize = 14.sp)
                    }
                }
            } else {

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                ) {
                    Text(
                        "This request has been processed and is currently: ${statusText.uppercase()}",
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


@Composable
fun DetailRowMinimal(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text("$label: ", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 12.sp, color = Color.Black)
    }
}