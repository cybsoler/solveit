package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    viewModel: MainViewModel,
    levelNumber: Int,
    onBack: () -> Unit,
    onComplete: (stars: Int, timeTaken: Long, hints: Int) -> Unit
) {
    val puzzle by viewModel.currentPuzzle.collectAsStateWithLifecycle()
    val isLoading by viewModel.puzzleLoading.collectAsStateWithLifecycle()

    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hintsUsed by remember { mutableIntStateOf(0) }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var answeredCorrectly by remember { mutableStateOf(false) }
    var showExplanation by remember { mutableStateOf(false) }

    LaunchedEffect(levelNumber) {
        viewModel.clearPuzzle()
        viewModel.loadPuzzle(levelNumber)
        startTime = System.currentTimeMillis()
    }

    Scaffold(
        containerColor = Slate950,
        topBar = {
            TopAppBar(
                title = { Text("LEVEL $levelNumber", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate300)
                    }
                },
                actions = {
                    if (puzzle != null && hintsUsed < puzzle!!.hints.size) {
                        IconButton(onClick = { 
                            hintsUsed++
                        }) {
                            Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = Amber400)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate950)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading || puzzle == null) {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator(color = Indigo400)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Generating your math puzzle...", color = Slate400, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.weight(1f))
            } else {
                val p = puzzle!!
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Text(
                        text = p.question,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp
                    )
                }

                if (hintsUsed > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 0 until hintsUsed) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Amber400.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                    .border(1.dp, Amber400.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "HINT ${i + 1}: ${p.hints[i]}",
                                    color = Amber400,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                p.options.forEachIndexed { index, option ->
                    val isSelected = selectedOptionIndex == index
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .background(
                                color = if (isSelected) Indigo900.copy(alpha = 0.5f) else Slate900.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Indigo500 else Slate800,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .selectable(
                                selected = isSelected,
                                onClick = { if(!answeredCorrectly) selectedOptionIndex = index },
                                role = Role.RadioButton
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                                enabled = !answeredCorrectly,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Indigo400,
                                    unselectedColor = Slate500
                                )
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = option,
                                color = if (isSelected) Color.White else Slate300,
                                fontSize = 18.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (!answeredCorrectly) {
                    Button(
                        onClick = {
                            if (selectedOptionIndex == p.correctAnswerIndex) {
                                answeredCorrectly = true
                                showExplanation = true
                            } else {
                                // show error or shake
                            }
                        },
                        enabled = selectedOptionIndex != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Indigo500,
                            contentColor = Color.White,
                            disabledContainerColor = Slate800,
                            disabledContentColor = Slate500
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("SUBMIT ANSWER", fontWeight = FontWeight.Black, fontSize = 18.sp, letterSpacing = 1.sp)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Purple600.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                            .border(1.dp, Purple500, RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Column {
                            Text("CORRECT!", color = Purple300, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(p.explanation, color = Color.White, fontSize = 16.sp, lineHeight = 24.sp)
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            val timeTaken = (System.currentTimeMillis() - startTime) / 1000
                            var stars = 3
                            if (hintsUsed > 0) stars--
                            if (timeTaken > 60) stars--
                            if (stars < 1) stars = 1
                            onComplete(stars, timeTaken, hintsUsed)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Slate950
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("CONTINUE", fontWeight = FontWeight.Black, fontSize = 18.sp, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}
