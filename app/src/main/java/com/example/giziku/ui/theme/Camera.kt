package com.example.giziku.ui.theme

import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.giziku.R
import com.example.giziku.util.CameraViewModel
import com.example.giziku.util.Nutrisi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CameraView(
    viewModel: CameraViewModel,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    overlay: @Composable () -> Unit = {}
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val lensFacing    by viewModel.lensFacing.collectAsStateWithLifecycle()
    val context       = LocalContext.current

    // State to hold the parsed Nutrition (or null if none)
    var nutritionData by remember { mutableStateOf<Nutrisi?>(null) }

    LaunchedEffect(lifecycleOwner, lensFacing) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    surfaceRequest?.let {
        Box(modifier = modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        viewModel.cameraPreviewUseCase.surfaceProvider = surfaceProvider
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            overlay()

            Button(
                onClick = {
                    viewModel.captureAndProcess(context) { jsonResult ->
                        // 2. Try parsing the JSON into the data class
                        kotlin.runCatching {
                            val cleaned = jsonResult.stripCodeFences()
                            if (cleaned == "null" || cleaned.isBlank()) {
                                // Tidak ada data makanan, abaikan—atau tampilkan pesan "Tidak terdeteksi makanan"
                                return@captureAndProcess
                            }
                            kotlinx.serialization.json.Json.decodeFromString<Nutrisi>(cleaned)
                        }.onSuccess { parsed ->
                            nutritionData = parsed      // trigger dialog
                        }.onFailure {
                            // parsing failed, you can log or show a toast
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor   = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.kamera),
                    contentDescription = "Capture"
                )
            }
        }

        // 3. Show the AlertDialog when nutritionData is non-null
        if (nutritionData != null) {
            AlertDialog(
                onDismissRequest = { nutritionData = null },
                title   = { Text("Hasil Analisis Makanan") },
                text    = {
                    Column {
                        Text("Kalori     : ${nutritionData!!.kalori}")
                        Text("Protein    : ${nutritionData!!.protein}")
                        Text("Karbohidrat: ${nutritionData!!.karbohidrat}")
                        Text("Lemak      : ${nutritionData!!.lemak}")
                        Text("Serat      : ${nutritionData!!.serat}")
                        Text("Gula       : ${nutritionData!!.gula}")
                        Text("Natrium    : ${nutritionData!!.natrium}")
                        Text("Kolesterol : ${nutritionData!!.kolesterol}")
                        Text("Vitamin    : ${nutritionData!!.vitamin}")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { nutritionData = null }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

fun String.stripCodeFences(): String {
    var s = this.trim()
        .removePrefix("```json")
        .removePrefix("``` json")
        .removePrefix("```")
    if (s.endsWith("```")) {
        s = s.substring(0, s.length - 3)
    }
    return s.trim()
}