package com.example.hrapplication.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hrapplication.ui.theme.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onRoleSelected: (String) -> Unit,
    viewModel: RequestViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }


    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF9F0F0),
            Color(0xFFE2D1D1),
            Color(0xFF8A282A)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Welcome Back",
                    color = Color(0xFF8A282A),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "Sign in to continue to HR Portal",
                    color = Color(0xFF444444),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 15.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    Column {
                        Text(
                            "University Email",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorMessage = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            placeholder = { Text("name.surname@fet.ba", fontSize = 15.sp) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF8A282A)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                autoCorrectEnabled = false
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8A282A),
                                unfocusedBorderColor = Color(0xFFEEEEEE),
                                focusedContainerColor = Color(0xFFF9F0F0).copy(alpha = 0.3f),
                                unfocusedContainerColor = Color.Transparent
                            ),
                            singleLine = true,
                            isError = errorMessage != null
                        )
                    }


                    Column {
                        Text(
                            "Password",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                autoCorrectEnabled = false
                            ),
                            placeholder = { Text("••••••••", fontSize = 15.sp) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF8A282A)) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color.LightGray
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8A282A),
                                unfocusedBorderColor = Color(0xFFEEEEEE),
                                focusedContainerColor = Color(0xFFF9F0F0).copy(alpha = 0.3f),
                                unfocusedContainerColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFF8A282A),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))


                    Button(
                        onClick = {
                            val inputEmail = email.trim()
                            val inputPass = password.trim()
                            if (inputEmail.isNotEmpty() && inputPass.isNotEmpty()) {
                                isLoading = true
                                viewModel.performLogin(inputEmail, inputPass) { role ->
                                    isLoading = false
                                    if (role != null) {
                                        onRoleSelected(role)
                                    } else {
                                        errorMessage = "Invalid email or password."
                                    }
                                }
                            } else {
                                errorMessage = "Please enter both fields."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A282A)),
                        enabled = !isLoading,
                        elevation = ButtonDefaults.buttonColors().let { ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp) }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                        } else {
                            Text("SIGN IN", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "University of Tuzla",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
