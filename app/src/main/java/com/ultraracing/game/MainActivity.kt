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
import io.github.sceneview.SceneView
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberNodes
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

    var selectedCar by remember {
        mutableStateOf("M'r ApurbA Car")
    }

    var speed by remember {
        mutableIntStateOf(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080D18))
    ) {

        // 3D CAR VIEW
        SceneView(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraManipulator = rememberCameraManipulator(),
            childNodes = rememberNodes {
                add(
                    ModelNode(
                        modelInstance = modelLoader.createModelInstance(
                            "models/apurba_car.glb"
                        ),
                        scaleToUnits = 1.0f
                    )
                )
            }
        )

        // TOP INFORMATION
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(18.dp)
        ) {

            Text(
                text = "ULTRA RACING GAME",
                color = Color.Cyan,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = selectedCar,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "FIRST EDITION",
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }

        // SPEED DISPLAY
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
                Text(
                    text = "SPEED",
                    color = Color.Cyan,
                    fontSize = 13.sp
                )

                Text(
                    text = "$speed",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "KM/H",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }
        }

        // CONTROL BUTTONS
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        speed = (speed - 10).coerceAtLeast(0)
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00BCD4)
                    )
                ) {
                    Text(
                        "ACCELERATE",
                        color = Color.Black
                    )
                }
            }

            Button(
                onClick = {
                    speed = 0
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                Text("RESET")
            }
        }
    }
}