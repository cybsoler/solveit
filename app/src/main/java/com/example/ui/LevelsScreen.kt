package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelsScreen(viewModel: MainViewModel, onLevelClick: (Int) -> Unit) {
    val levels by viewModel.allLevels.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Slate950,
        topBar = { 
            TopAppBar(
                title = { Text("ALL LEVELS", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate950)
            ) 
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(levels) { level ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            if (level.unlocked) Indigo900.copy(alpha = 0.6f)
                            else Slate900.copy(alpha = 0.4f)
                        )
                        .clickable(enabled = level.unlocked) {
                            onLevelClick(level.levelNumber)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!level.unlocked) {
                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Slate500, modifier = Modifier.size(32.dp))
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${level.levelNumber}",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.Center) {
                                if (level.stars > 0) {
                                    for (i in 1..level.stars) {
                                        Icon(
                                            Icons.Default.Star, 
                                            contentDescription = "Star", 
                                            modifier = Modifier.size(14.dp),
                                            tint = Amber400
                                        )
                                    }
                                } else {
                                    Text("PLAY", color = Indigo300, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
