package com.example.hrapplication.ui.theme.screens
import com.example.hrapplication.data.RequestItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeanDetailsScreen(
    request: RequestItem,
    onFinalProcess: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var deanComment by remember { mutableStateOf(request.deanComment) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F0F0))
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Details", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8A282A))
        Text("Request #${request.id.take(3)}", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        InfoCard(title = "General Information", icon = { Icon(Icons.Default.PersonOutline, null) }) {
            val dates = request.date.split(" - ")
            val startDateDisplay = dates.getOrNull(0)?.trim()?.takeIf { it.isNotEmpty() } ?: request.date
            val endDateDisplay = dates.getOrNull(1)?.trim()?.takeIf { it.isNotEmpty() } ?: "N/A"
            DetailRow("Request type", request.type)
            DetailRow("Start date", startDateDisplay)
            DetailRow("End date", endDateDisplay)
            DetailRow("Note", request.note.ifEmpty { "No note provided" })
        }
        Spacer(modifier = Modifier.height(16.dp))
        InfoCard(title = "HR Comment", icon = { Icon(Icons.Default.AccessTime, null) }) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)).padding(8.dp)
            ) {
                Text(
                    text = if (request.hrComment.isNotEmpty()) request.hrComment else "No HR comment provided.",
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        InfoCard(title = "Comment", icon = { Icon(Icons.Default.Edit, null) }) {
            OutlinedTextField(
                value = deanComment,
                onValueChange = { deanComment = it },
                placeholder = { Text("Enter your comment ...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))


        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { onFinalProcess(deanComment, "Approved") },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF437A54)),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Approve") }

            Button(
                onClick = { onFinalProcess(deanComment, "Denied") },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A282A)),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Deny") }
        }
    }
}