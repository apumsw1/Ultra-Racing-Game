package com.ultraracing.game

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
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
    var accelerating by remember { mutableStateOf(false) }
    var braking by remember { mutableStateOf(false) }
    var gyroEnabled by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var steering by remember { mutableFloatStateOf(0f) }
    var roadPosition by remember { mutableFloatStateOf(0f) }

    val context = androidx.compose.ui.platform.LocalContext.current

    // Gyroscope-based phone orientation steering
    DisposableEffect(gyroEnabled) {
        val manager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val sensor = manager.getDefaultSensor(
            Sensor.TYPE_ROTATION_VECTOR
        )

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (!gyroEnabled) return

                val matrix = FloatArray(9)
                val orientation = FloatArray(3)

                SensorManager.getRotationMatrixFromVector(
                    matrix,
                    event.values
                )

                SensorManager.getOrientation(
                    matrix,
                    orientation
                )

                // Phone tilt angle
                val roll = orientation[2]

                steering = (roll / 0.65f).coerceIn(-1f, 1f)
            }

            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int
            ) {}
        }

        if (gyroEnabled && sensor != null) {
            manager.registerListener(
                listener,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            manager.unregisterListener(listener)
        }
    }

    // Driving physics: acceleration and braking
    LaunchedEffect(accelerating, braking) {
        while (accelerating || braking) {
            delay(50)

            when {
                braking -> speed = (speed - 3).coerceAtLeast(0)
                accelerating -> speed = (speed + 1).coerceAtMost(320)
            }
        }
    }

    // Road movement
    LaunchedEffect(Unit) {
        while (true) {
            delay(40)

            if (speed > 0) {
                roadPosition += speed / 150f
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Dark)
    ) {
        val w = maxWidth
        val h = maxHeight

        // GAME WORLD
        Canvas(Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val bend = steering * width * 0.12f

            // Sky
            drawRect(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF071A38),
                        Color(0xFF16466A),
                        Color(0xFF6C8292),
                        Color(0xFF172536)
                    )
                )
            )

            // Sun
            drawCircle(
                Color(0xFFFFC56B),
                height * 0.065f,
                Offset(width * 0.78f, height * 0.22f)
            )

            // Mountains
            val mountain = Path().apply {
                moveTo(0f, height * 0.53f)
                lineTo(width * 0.12f, height * 0.31f)
                lineTo(width * 0.24f, height * 0.48f)
                lineTo(width * 0.38f, height * 0.28f)
                lineTo(width * 0.54f, height * 0.50f)
                lineTo(width * 0.72f, height * 0.30f)
                lineTo(width, height * 0.49f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(mountain, Color(0xFF172E42))

            // Grass
            drawRect(
                Color(0xFF173D31),
                topLeft = Offset(0f, height * 0.53f),
                size = Size(width, height * 0.47f)
            )

            // Road
            val road = Path().apply {
                moveTo(width * 0.46f + bend * 0.35f, height * 0.43f)
                lineTo(width * 0.54f + bend * 0.35f, height * 0.43f)
                lineTo(width * 0.91f + bend, height)
                lineTo(width * 0.09f + bend, height)
                close()
            }

            drawPath(road, Color(0xFF252B35))

            // Road edges
            drawLine(
                Color.White,
                Offset(width * 0.46f + bend * 0.35f, height * 0.43f),
                Offset(width * 0.09f + bend, height),
                5f
            )

            drawLine(
                Color.White,
                Offset(width * 0.54f + bend * 0.35f, height * 0.43f),
                Offset(width * 0.91f + bend, height),
                5f
            )

            // Lane markings
            for (i in 0..10) {
                val progress =
                    ((i / 10f + roadPosition * 0.025f) % 1f)

                val y = height * (
                    0.43f + progress * progress * 0.57f
                )

                val halfWidth =
                    width * (0.008f + progress * 0.025f)

                drawRoundRect(
                    Color.White,
                    topLeft = Offset(
                        width * 0.5f - halfWidth / 2 + bend * progress,
                        y
                    ),
                    size = Size(
                        halfWidth,
                        5f + progress * 18f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                )
            }

            // Car shadow
            drawOval(
                Color.Black.copy(alpha = 0.55f),
                topLeft = Offset(
                    width * 0.34f + bend * 0.8f,
                    height * 0.79f
                ),
                size = Size(width * 0.32f, height * 0.14f)
            )

            // Car body
            val car = Path().apply {
                moveTo(width * 0.32f + bend * 0.8f, height * 0.91f)
                lineTo(width * 0.34f + bend * 0.8f, height * 0.84f)
                lineTo(width * 0.40f + bend * 0.8f, height * 0.80f)
                lineTo(width * 0.44f + bend * 0.8f, height * 0.72f)
                lineTo(width * 0.56f + bend * 0.8f, height * 0.72f)
                lineTo(width * 0.60f + bend * 0.8f, height * 0.80f)
                lineTo(width * 0.66f + bend * 0.8f, height * 0.84f)
                lineTo(width * 0.68f + bend * 0.8f, height * 0.91f)
                lineTo(width * 0.63f + bend * 0.8f, height * 0.96f)
                lineTo(width * 0.37f + bend * 0.8f, height * 0.96f)
                close()
            }

            drawPath(
                car,
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFF465D),
                        Color(0xFFD90028),
                        Color(0xFF650016)
                    )
                )
            )

            // Windows
            val window = Path().apply {
                moveTo(width * 0.405f + bend * 0.8f, height * 0.81f)
                lineTo(width * 0.445f + bend * 0.8f, height * 0.745f)
                lineTo(width * 0.555f + bend * 0.8f, height * 0.745f)
                lineTo(width * 0.595f + bend * 0.8f, height * 0.81f)
                close()
            }

            drawPath(window, Color(0xFF071A2C))

            // Headlights
            drawRoundRect(
                Color(0xFFBDFBFF),
                Offset(width * 0.345f + bend * 0.8f, height * 0.865f),
                Size(width * 0.075f, height * 0.022f),
                androidx.compose.ui.geometry.CornerRadius(8f)
            )

            drawRoundRect(
                Color(0xFFBDFBFF),
                Offset(width * 0.58f + bend * 0.8f, height * 0.865f),
                Size(width * 0.075f, height * 0.022f),
                androidx.compose.ui.geometry.CornerRadius(8f)
            )

            // Grill
            drawRoundRect(
                Color(0xFF10151D),
                Offset(width * 0.44f + bend * 0.8f, height * 0.89f),
                Size(width * 0.12f, height * 0.035f),
                androidx.compose.ui.geometry.CornerRadius(12f)
            )

            // Wheels
            for (x in listOf(0.35f, 0.61f)) {
                drawRoundRect(
                    Color.Black,
                    Offset(width * x + bend * 0.8f, height * 0.85f),
                    Size(width * 0.045f, height * 0.11f),
                    androidx.compose.ui.geometry.CornerRadius(10f)
                )
            }
        }

        // SPEEDOMETER
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
                    vertical = 10.dp
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
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    "KM/H",
                    color = Green,
                    fontSize = 12.sp
                )
            }
        }

        // SETTINGS BUTTON
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .clickable { showSettings = true },
            shape = RoundedCornerShape(12.dp),
            color = Color(0xDD071526)
        ) {
            Text(
                "⚙ SETTINGS",
                modifier = Modifier.padding(
                    horizontal = 15.dp,
                    vertical = 11.dp
                ),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        // SETTINGS DIALOG
        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                containerColor = Color(0xFF101D30),
                title = {
                    Text(
                        "DRIVING SETTINGS",
                        color = Color.White
                    )
                },
                text = {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Gyroscope Steering",
                                color = Color.White
                            )

                            Switch(
                                checked = gyroEnabled,
                                onCheckedChange = {
                                    gyroEnabled = it
                                    steering = 0f
                                }
                            )
                        }

                        Text(
                            if (gyroEnabled)
                                "GYRO ON - Tilt your phone to steer"
                            else
                                "GYRO OFF - Touch steering mode",
                            color = Cyan,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showSettings = false }
                    ) {
                        Text("DONE", color = Cyan)
                    }
                }
            )
        }

        // GAME TITLE
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "ULTRA RACING GAME",
                color = Cyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
        }

        // ACCELERATOR TOGGLE
        ControlButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 18.dp),
            icon = "▲",
            label = "ACCELERATOR",
            active = accelerating,
            color = Green,
            onClick = {
                accelerating = !accelerating
                if (accelerating) braking = false
            }
        )

        // BRAKE TOGGLE
        ControlButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 18.dp),
            icon = "■",
            label = "BRAKE",
            active = braking,
            color = Red,
            onClick = {
                braking = !braking
                if (braking) accelerating = false
            }
        )

        // TOUCH STEERING BUTTONS (visible only when gyro is OFF)
        if (!gyroEnabled) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SteeringButton("◀") {
                    steering = (steering - 0.25f).coerceIn(-1f, 1f)
                }

                SteeringButton("▶") {
                    steering = (steering + 0.25f).coerceIn(-1f, 1f)
                }
            }
        }

        // RESET
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
                .clickable {
                    speed = 0
                    accelerating = false
                    braking = false
                    steering = 0f
                    roadPosition = 0f
                },
            shape = RoundedCornerShape(10.dp),
            color = Color(0xDD101D30)
        ) {
            Text(
                "↻ RESET",
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 8.dp
                ),
                color = Color.White,
                fontSize = 11.sp,
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
    active: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .background(
                if (active) color.copy(alpha = 0.30f)
                else Color(0xDD071526),
                RoundedCornerShape(18.dp)
            )
            .then(
                if (active) Modifier.background(
                    color.copy(alpha = 0.12f),
                    RoundedCornerShape(18.dp)
                ) else Modifier
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

        if (active) {
            Text(
                "ACTIVE",
                color = color,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun SteeringButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(58.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        color = Color(0xDD071526)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label,
                color = Cyan,
                fontSize = 27.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}