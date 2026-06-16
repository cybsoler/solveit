package com.example.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(viewModel: MainViewModel) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val generatedImageBase64 by viewModel.generatedImageBase64.collectAsStateWithLifecycle()
    val imageLoading by viewModel.imageLoading.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var generationPrompt by remember { mutableStateOf("A super advanced math robot mascot") }
    var selectedSize by remember { mutableStateOf("2K") }
    val sizes = listOf("1K", "2K", "4K")

    Scaffold(
        containerColor = Slate950,
        topBar = { 
            TopAppBar(
                title = { Text("PROFILE", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate950)
            ) 
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            if (generatedImageBase64 != null) {
                val bitmapInfo = remember(generatedImageBase64) {
                    try {
                        val bytes = Base64.decode(generatedImageBase64, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                    } catch(e: Exception) {
                        null
                    }
                }
                
                if (bitmapInfo != null) {
                    Image(
                        bitmap = bitmapInfo,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 16.dp)
                            .clip(CircleShape)
                            .border(2.dp, Indigo500, CircleShape)
                    )
                } else {
                    Text("Failed to load image", color = MaterialTheme.colorScheme.error)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(Indigo500, Purple600)),
                            shape = CircleShape
                        )
                        .border(2.dp, Slate800, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("JD", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            if (imageLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp), color = Indigo400)
            } else {
                OutlinedButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.padding(bottom = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Indigo300),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Indigo500.copy(alpha = 0.5f))
                ) {
                    Text("GENERATE AVATAR", fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(Slate900.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Text("PROFILE INFO", color = Slate500, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(profile.displayName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Top Level: ${profile.currentLevel}", color = Slate400, fontSize = 16.sp)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(Slate900.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("DAILY STREAK", color = Slate500, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("${profile.currentStreak} Days", color = Orange400, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    }
                    Box(modifier = Modifier.size(64.dp).background(Orange400.copy(alpha = 0.1f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Text("🔥", fontSize = 32.sp)
                    }
                }
            }

            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
                onClick = {
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, "I'm on level ${profile.currentLevel} of Math Prodigy with a streak of ${profile.currentStreak} days! Can you beat me?")
                    }
                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Progress"))
                },
                modifier = Modifier.fillMaxWidth().height(64.dp).padding(bottom = 16.dp),
                contentPadding = PaddingValues(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo500, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share")
                Spacer(Modifier.width(8.dp))
                Text("SHARE PROGRESS", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Generate Avatar") },
            text = {
                Column {
                    OutlinedTextField(
                        value = generationPrompt,
                        onValueChange = { generationPrompt = it },
                        label = { Text("Prompt") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Size")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sizes.forEach { size ->
                            FilterChip(
                                selected = selectedSize == size,
                                onClick = { selectedSize = size },
                                label = { Text(size) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.requestImage(generationPrompt, selectedSize)
                    showDialog = false
                }) {
                    Text("Generate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
