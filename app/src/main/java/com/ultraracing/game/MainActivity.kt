package com.ultraracing.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UltraRacingGame()
        }
    }
}

@Composable
fun UltraRacingGame() {

    var gameStarted by remember { mutableStateOf(false) }

    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF080D1B),
            Color(0xFF111D35),
            Color(0xFF05070D)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Text(
                text = "ULTRA RACING",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = "GAME",
                color = Color(0xFF00E5FF),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = if (gameStarted)
                    "RACING MODE READY"
                else
                    "WELCOME TO THE RACING WORLD",
                color = Color.LightGray,
                fontSize = 15.sp
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(110.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF172642)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "3D RACING ENGINE",
                        color = Color(0xFF00E5FF),
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = { gameStarted = !gameStarted },
                modifier = Modifier
                    .width(240.dp)
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00B8D4)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (gameStarted) "BACK" else "START RACE",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "OFFLINE • SINGLE PLAYER",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}