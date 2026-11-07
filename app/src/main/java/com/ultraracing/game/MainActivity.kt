
package com.ultraracing.game

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.delay

private val Dark = Color(0xFF050B16)
private val Cyan = Color(0xFF00E5FF)
private val Green = Color(0xFF00FF9D)
private val Red = Color(0xFFFF3548)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation =
            android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

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

    val context = androidx.compose.ui.platform.LocalContext.current

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    DisposableEffect(gyroEnabled) {
        val manager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val sensor =
            manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (!gyroEnabled) return

                val matrix = FloatArray(9)
                val orientation = FloatArray(3)

                SensorManager.getRotationMatrixFromVector(
                    matrix,
                    event.values
                )

                SensorManager.getOrientation(matrix, orientation)

                steering = (orientation[2] / 0.65f)
                    .coerceIn(-1f, 1f)
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

    LaunchedEffect(accelerating, braking) {
        while (accelerating || braking) {
            delay(50)

            when {
                braking -> speed = (speed - 3).coerceAtLeast(0)
                accelerating -> speed = (speed + 1).coerceAtMost(320)
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Dark)
    ) {
        SceneView(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader
        ) {
            rememberModelInstance(
                modelLoader,
                "spectral_gt_rs_game_ready.glb"
            )?.let { instance ->
                ModelNode(
                    modelInstance = instance,
                    scaleToUnits = 2.0f,
                    autoAnimate = true
                )
            }
        }

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

        Text(
            "ULTRA RACING GAME",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 14.dp),
            color = Cyan,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black
        )

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

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp)
                .clickable {
                    speed = 0
                    accelerating = false
                    braking = false
                    steering = 0f
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

        ControlButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 18.dp),
            icon = "■",
            label = "BRAKE",
            active = braking,
            color = Red
        ) {
            braking = !braking
            if (braking) accelerating = false
        }

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

        ControlButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 18.dp),
            icon = "▲",
            label = "ACCELERATOR",
            active = accelerating,
            color = Green
        ) {
            accelerating = !accelerating
            if (accelerating) braking = false
        }

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

                        Spacer(Modifier.height(8.dp))

                        Text(
                            if (gyroEnabled)
                                "GYRO ON - Tilt phone to steer"
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
