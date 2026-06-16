package com.example.ui

import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LevelCompleteScreen(
    levelNumber: Int,
    stars: Int,
    onNext: () -> Unit,
    onReplay: () -> Unit,
    onHome: () -> Unit
) {
    val context = LocalContext.current
    val jokes = listOf(
        "You're a math magician! Did you pull that answer out of a hat?",
        "Einstein called, he wants his brain back!",
        "Calculators are jealous of you right now.",
        "You didn't just solve that, you dominated it!"
    )
    val quote = jokes[levelNumber % jokes.size]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(listOf(Indigo900.copy(alpha = 0.5f), Slate900.copy(alpha = 0.8f))),
                    shape = RoundedCornerShape(40.dp)
                )
                .border(2.dp, Indigo500.copy(alpha = 0.4f), RoundedCornerShape(40.dp))
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LEVEL COMPLETE",
                    color = Indigo300,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Level $levelNumber",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(32.dp))
                
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    for (i in 1..3) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(64.dp)
                                .background(
                                    if (i <= stars) Amber400.copy(alpha = 0.1f) else Slate800,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (i <= stars) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Star $i",
                                tint = if (i <= stars) Amber400 else Slate600,
                                modifier = Modifier.size(if (i <= stars) 48.dp else 32.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
                
                Text(
                    text = quote,
                    color = Slate300,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }
        
        Spacer(Modifier.height(32.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Slate950
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("NEXT LEVEL", fontWeight = FontWeight.Black, fontSize = 18.sp, letterSpacing = 1.sp)
        }
        
        Spacer(Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onReplay,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo500, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Replay")
            }
            
            Button(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "I just completed Level $levelNumber with $stars stars! Can you beat me?")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Achievement"))
                },
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple600, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate400),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("BACK TO MENU", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
