package com.example.hrapplication.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hrapplication.ui.theme.viewmodel.RequestViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreen(
    onBack: () -> Unit,
    onSubmit: (String, String, String) -> Unit,
    requestViewModel: RequestViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    var type by remember { mutableStateOf("Vacation") }
    var startDate by remember { mutableStateOf("") }
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var showPicker by remember { mutableStateOf(false) }
    var pickingForStart by remember { mutableStateOf(true) }
    var showWarningPopup by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                if (type == "Sick Leave") return true

                if (requestViewModel.holidayMillis.contains(utcTimeMillis)) return false

                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = utcTimeMillis

                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return false

                if (!pickingForStart && startDateMillis != null) {
                    return utcTimeMillis >= startDateMillis!!
                }


                val daysToAdd = if (type == "Vacation") 7 else 3
                val minValidDate = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    add(Calendar.DAY_OF_YEAR, daysToAdd)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                return utcTimeMillis >= minValidDate.timeInMillis
            }
        }
    )

    val options = listOf("Vacation", "Sick Leave", "Remote Work", "Business Trip")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF9F0F0))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "New request",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8A282A),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Column {
                        Text("Request type", fontSize = 14.sp, color = Color(0xFF444444), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = type,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.DarkGray) },
                                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF999999), focusedBorderColor = Color(0xFF8A282A))
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            type = option
                                            startDate = ""; endDate = ""; startDateMillis = null
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }


                    CustomDateInputField(label = "Start date", value = startDate) {
                        pickingForStart = true
                        showPicker = true
                    }

                    CustomDateInputField(
                        label = "End date",
                        value = endDate,
                        enabled = startDate.isNotEmpty()
                    ) {
                        pickingForStart = false
                        showPicker = true
                    }


                    Column {
                        Text("Comment", fontSize = 14.sp, color = Color(0xFF444444), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = { Text("Enter your comment", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFF999999), focusedBorderColor = Color(0xFF8A282A))
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))


                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { 

                                if (type == "Vacation" && startDateMillis != null) {
                                    val sevenDaysOut = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                        add(Calendar.DAY_OF_YEAR, 6) // Današnji dan + 6 punih dana = 7. dan
                                        set(Calendar.HOUR_OF_DAY, 0)
                                        set(Calendar.MINUTE, 0)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }.timeInMillis

                                    if (startDateMillis!! < sevenDaysOut) {
                                        showWarningPopup = true
                                        return@Button
                                    }
                                }
                                
                                println("Submit kliknut: $type, $startDate - $endDate")
                                onSubmit(type, "$startDate - $endDate", note)
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A282A)),
                            shape = RoundedCornerShape(12.dp),
                            enabled = startDate.isNotEmpty() && endDate.isNotEmpty()
                        ) {
                            Text("Submit", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onBack,
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF828282)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (showWarningPopup) {
            AlertDialog(
                onDismissRequest = { showWarningPopup = false },
                confirmButton = {
                    TextButton(onClick = { showWarningPopup = false }) {
                        Text("OK", color = Color(0xFF8A282A), fontWeight = FontWeight.Bold)
                    }
                },
                title = { Text("Notice") },
                text = { Text("Vacation requests must be submitted at least 7 days in advance.") },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }

        if (showPicker) {
            DatePickerDialog(
                onDismissRequest = { showPicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            val formattedDate = sdf.format(Date(millis))

                            if (pickingForStart) {
                                startDate = formattedDate
                                startDateMillis = millis
                                endDate = ""
                            } else {
                                endDate = formattedDate
                            }
                        }
                        showPicker = false
                    }) { Text("OK", color = Color(0xFF8A282A), fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showPicker = false }) { Text("Cancel", color = Color.Gray) }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun CustomDateInputField(label: String, value: String, enabled: Boolean = true, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable(enabled = enabled) { onClick() }) {
        Text(label, fontSize = 14.sp, color = if(enabled) Color(0xFF444444) else Color.LightGray, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("dd.mm.yyyy", color = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            trailingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = if(enabled) Color.DarkGray else Color.LightGray) },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = if(enabled) Color.Black else Color.LightGray,
                disabledBorderColor = if(enabled) Color(0xFF999999) else Color(0xFFEEEEEE),
                disabledPlaceholderColor = Color.Gray
            )
        )
    }
}