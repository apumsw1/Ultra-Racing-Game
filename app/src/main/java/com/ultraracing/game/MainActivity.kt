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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import io.github.sceneview.Scene
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.node.ModelNode

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                UltraRacingGame()
            }
        }
    }
}

@Composable
fun UltraRacingGame() {

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    var speed by remember {
        mutableIntStateOf(0)
    }

    var isAccelerating by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080D18))
    ) {

        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraManipulator = rememberCameraManipulator()
        ) {

            rememberModelInstance(
                modelLoader,
                "models/apurba_car.glb"
            )?.let { instance ->

                ModelNode(
                    modelInstance = instance,
                    scaleToUnits = 1.0f,
                    autoAnimate = true
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
        ) {

            Text(
                text = "ULTRA RACING GAME",
                color = Color.Cyan,
                fontSize = 25.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "M'r ApurbA Car",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "FIRST EDITION",
                color = Color(0xFF00FF9D),
                fontSize = 13.sp
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xDD101E32)
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "SPEED",
                    color = Color.Cyan,
                    fontSize = 14.sp
                )

                Text(
                    text = "$speed",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "KM/H",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = {
                    speed = (speed - 10).coerceAtLeast(0)
                    isAccelerating = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF263B58)
                )
            ) {
                Text("BRAKE")
            }

            Button(
                onClick = {
                    speed = (speed + 10).coerceAtMost(300)
                    isAccelerating = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF)
                )
            ) {
                Text(
                    "ACCELERATE",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    speed = 0
                    isAccelerating = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                Text("RESET")
            }
        }

        if (isAccelerating) {
            Text(
                text = "ENGINE ACTIVE",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 85.dp),
                color = Color(0xFF00FF9D),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}