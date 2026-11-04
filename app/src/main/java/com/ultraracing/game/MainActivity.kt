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

import io.github.sceneview.SceneView
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.node.ModelNode

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApurbaCarScreen()
        }
    }
}

@Composable
fun ApurbaCarScreen() {

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    var speed by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080D18))
    ) {

        SceneView(
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
                .padding(18.dp)
        ) {
            Text(
                "ULTRA RACING GAME",
                color = Color.Cyan,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                "M'r ApurbA Car",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "FIRST EDITION",
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xDD101E32)
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("SPEED", color = Color.Cyan)
                Text(
                    "$speed",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text("KM/H", color = Color.LightGray)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = {
                    speed = (speed - 10).coerceAtLeast(0)
                }
            ) {
                Text("BRAKE")
            }

            Button(
                onClick = {
                    speed = (speed + 10).coerceAtMost(300)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Cyan
                )
            ) {
                Text("ACCELERATE", color = Color.Black)
            }

            Button(
                onClick = { speed = 0 }
            ) {
                Text("RESET")
            }
        }
    }
}