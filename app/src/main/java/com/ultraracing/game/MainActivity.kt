package com.ultraracing.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlin.math.*

private val Dark = Color(0xFF050B16)
private val Cyan = Color(0xFF00E5FF)
private val Green = Color(0xFF00FF9D)
private val Red = Color(0xFFFF3548)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK

        setContent {
            MaterialTheme {
                RacingScreen()
            }
        }
    }
}

@Composable
fun RacingScreen() {

    var speed by remember { mutableIntStateOf(0) }
    var roadPosition by remember { mutableFloatStateOf(0f) }
    var accelerating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(40)

            if (accelerating && speed > 0) {
                roadPosition += speed / 150f
            }
        }
    }

    LaunchedEffect(accelerating) {
        while (accelerating) {
            delay(150)
            speed = (speed + 1).coerceAtMost(320)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Dark)
    ) {

        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val w = size.width
            val h = size.height

            // SKY

            drawRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF071A38),
                        Color(0xFF16466A),
                        Color(0xFF6C8292),
                        Color(0xFF172536)
                    )
                )
            )

            // SUN

            drawCircle(
                color = Color(0xFFFFC56B),
                radius = h * 0.065f,
                center = androidx.compose.ui.geometry.Offset(
                    w * 0.78f,
                    h * 0.22f
                )
            )

            // MOUNTAINS

            val mountain = Path().apply {
                moveTo(0f, h * 0.53f)
                lineTo(w * 0.12f, h * 0.31f)
                lineTo(w * 0.24f, h * 0.48f)
                lineTo(w * 0.38f, h * 0.28f)
                lineTo(w * 0.54f, h * 0.50f)
                lineTo(w * 0.72f, h * 0.30f)
                lineTo(w, h * 0.49f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }

            drawPath(
                mountain,
                Color(0xFF172E42)
            )

            // GRASS

            drawRect(
                color = Color(0xFF173D31),
                topLeft = androidx.compose.ui.geometry.Offset(
                    0f, h * 0.53f
                ),
                size = androidx.compose.ui.geometry.Size(
                    w, h * 0.47f
                )
            )

            // ROAD

            val road = Path().apply {
                moveTo(w * 0.46f, h * 0.43f)
                lineTo(w * 0.54f, h * 0.43f)
                lineTo(w * 0.91f, h)
                lineTo(w * 0.09f, h)
                close()
            }

            drawPath(
                road,
                Color(0xFF252B35)
            )

            // ROAD EDGES

            drawLine(
                Color.White,
                androidx.compose.ui.geometry.Offset(
                    w * 0.46f, h * 0.43f
                ),
                androidx.compose.ui.geometry.Offset(
                    w * 0.09f, h
                ),
                strokeWidth = 5f
            )

            drawLine(
                Color.White,
                androidx.compose.ui.geometry.Offset(
                    w * 0.54f, h * 0.43f
                ),
                androidx.compose.ui.geometry.Offset(
                    w * 0.91f, h
                ),
                strokeWidth = 5f
            )

            // ROAD LANE MARKINGS

            for (i in 0..10) {

                val progress =
                    ((i / 10f + roadPosition * 0.025f) % 1f)

                val y =
                    h * (0.43f + progress * progress * 0.57f)

                val halfWidth =
                    w * (0.008f + progress * 0.025f)

                drawRoundRect(
                    color = Color.White,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        w * 0.5f - halfWidth / 2,
                        y
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        halfWidth,
                        5f + progress * 18f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        8f
                    )
                )
            }

            // ROAD LIGHTS

            for (i in 0..5) {

                val y = h * (0.48f + i * 0.105f)
                val scale = 0.3f + i * 0.15f

                drawCircle(
                    color = Cyan,
                    radius = 4f * scale,
                    center = androidx.compose.ui.geometry.Offset(
                        w * (0.35f - i * 0.045f),
                        y
                    )
                )

                drawCircle(
                    color = Cyan,
                    radius = 4f * scale,
                    center = androidx.compose.ui.geometry.Offset(
                        w * (0.65f + i * 0.045f),
                        y
                    )
                )
            }

            // SPORTS CAR SHADOW

            drawOval(
                color = Color.Black.copy(alpha = 0.55f),
                topLeft = androidx.compose.ui.geometry.Offset(
                    w * 0.34f,
                    h * 0.79f
                ),
                size = androidx.compose.ui.geometry.Size(
                    w * 0.32f,
                    h * 0.14f
                )
            )

            // SPORTS CAR BODY

            val car = Path().apply {

                moveTo(w * 0.32f, h * 0.91f)

                lineTo(w * 0.34f, h * 0.84f)

                lineTo(w * 0.40f, h * 0.80f)

                lineTo(w * 0.44f, h * 0.72f)

                lineTo(w * 0.56f, h * 0.72f)

                lineTo(w * 0.60f, h * 0.80f)

                lineTo(w * 0.66f, h * 0.84f)

                lineTo(w * 0.68f, h * 0.91f)

                lineTo(w * 0.63f, h * 0.96f)

                lineTo(w * 0.37f, h * 0.96f)

                close()
            }

            drawPath(
                car,
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFFF465D),
                        Color(0xFFD90028),
                        Color(0xFF650016)
                    )
                )
            )

            // CAR WINDOWS

            val window = Path().apply {

                moveTo(w * 0.405f, h * 0.81f)

                lineTo(w * 0.445f, h * 0.745f)

                lineTo(w * 0.555f, h * 0.745f)

                lineTo(w * 0.595f, h * 0.81f)

                close()
            }

            drawPath(
                window,
                Color(0xFF071A2C)
            )

            // FRONT GLASS REFLECTION

            drawLine(
                Color(0xFF5EEBFF),
                androidx.compose.ui.geometry.Offset(
                    w * 0.50f, h * 0.75f
                ),
                androidx.compose.ui.geometry.Offset(
                    w * 0.50f, h * 0.81f
                ),
                strokeWidth = 3f
            )

            // HEADLIGHTS

            drawRoundRect(
                Color(0xFFBDFBFF),
                topLeft = androidx.compose.ui.geometry.Offset(
                    w * 0.345f,
                    h * 0.865f
                ),
                size = androidx.compose.ui.geometry.Size(
                    w * 0.075f,
                    h * 0.022f
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
            )

            drawRoundRect(
                Color(0xFFBDFBFF),
                topLeft = androidx.compose.ui.geometry.Offset(
                    w * 0.58f,
                    h * 0.865f
                ),
                size = androidx.compose.ui.geometry.Size(
                    w * 0.075f,
                    h * 0.022f
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
            )

            // FRONT GRILL

            drawRoundRect(
                Color(0xFF10151D),
                topLeft = androidx.compose.ui.geometry.Offset(
                    w * 0.44f,
                    h * 0.89f
                ),
                size = androidx.compose.ui.geometry.Size(
                    w * 0.12f,
                    h * 0.035f
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
            )

            // CAR WHEELS

            for (x in listOf(0.35f, 0.61f)) {

                drawRoundRect(
                    Color.Black,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        w * x,
                        h * 0.85f
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        w * 0.045f,
                        h * 0.11f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f)
                )
            }
        }

        // LEFT SPEED METER

        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xDD071526)
            )
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 22.dp,
                    vertical = 12.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "SPEED",
                    color = Cyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "$speed",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    "KM/H",
                    color = Green,
                    fontSize = 12.sp
                )
            }
        }

        // RIGHT BRANDING

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {

            Text(
                "ULTRA RACING GAME",
                color = Cyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                "M'r ApurbA Car",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "SPORTS EDITION",
                color = Green,
                fontSize = 10.sp
            )
        }

        // ACCELERATOR

        ControlButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 25.dp,
                    bottom = 20.dp
                ),
            icon = "▲",
            label = "ACCELERATOR",
            color = Green,
            onClick = {
                accelerating = true
                speed = (speed + 10).coerceAtMost(320)
            }
        )

        // BRAKE

        ControlButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 25.dp,
                    bottom = 20.dp
                ),
            icon = "■",
            label = "BRAKE",
            color = Red,
            onClick = {
                accelerating = false
                speed = (speed - 30).coerceAtLeast(0)
            }
        )

        // RESET

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .clickable {
                    speed = 0
                    accelerating = false
                    roadPosition = 0f
                },
            shape = RoundedCornerShape(12.dp),
            color = Color(0xDD101D30)
        ) {

            Text(
                "↻ RESET",
                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 12.dp
                ),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ControlButton(
    modifier: Modifier,
    icon: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(
                Color(0xDD071526),
                RoundedCornerShape(18.dp)
            )
            .padding(
                horizontal = 22.dp,
                vertical = 12.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            icon,
            color = color,
            fontSize = 31.sp,
            fontWeight = FontWeight.Black
        )

        Text(
            label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}