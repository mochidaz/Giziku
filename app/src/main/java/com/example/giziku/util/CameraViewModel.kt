package com.example.giziku.util
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CameraViewModel : ViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val _lensFacing = MutableStateFlow(LENS_FACING_BACK)
    val lensFacing: StateFlow<Int> = _lensFacing

    // Tambahkan ImageCapture use case
    private val imageCaptureUseCase = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    // Preview use case tetap kita butuh untuk UI
    internal val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    fun toggleLensFacing() {
        _lensFacing.value = if (_lensFacing.value == LENS_FACING_FRONT) {
            LENS_FACING_BACK
        } else {
            LENS_FACING_FRONT
        }
    }

    /**
     * Bind semua use case (preview + capture) ke lifecycle kamera
     */
    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val cameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(_lensFacing.value)
            .build()

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            cameraPreviewUseCase,
            imageCaptureUseCase
        )

        try {
            awaitCancellation()
        } finally {
            cameraProvider.unbindAll()
        }
    }

    /**
     * Fungsi untuk menangkap foto, konversi ke Bitmap, kemudian kirim ke Gemini API
     */
    fun captureAndProcess(appContext: Context, onResult: (String) -> Unit) {
        // Buat temp file untuk capture (kita akan konversi ke bitmap lalu hapus)
        val file = File(appContext.cacheDir, "temp_capture.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCaptureUseCase.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(appContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    onResult("Capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Load file ke Bitmap
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    // Hapus file sementara
                    file.delete()

                    // Kirim ke API Gemini di background
                    viewModelScope.launch {
                        val response = modelCall(bitmap)
                        onResult(response)
                    }
                }
            }
        )
    }

    /**
     * Fungsi yang sudah disediakan untuk panggil Gemini
     */
    suspend fun modelCall(imageBitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val model = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = "AIzaSyCpJ61DKwKdbz2XfAGRuQLAGoQG1ykNR7k",
        )

        val content = Content.Builder()
            .image(imageBitmap)
            .text(
                """
                    Dari gambar makanan di atas, tampilkan:
                    - Kadar kalori
                    - Kadar protein
                    - Kadar karbohidrat
                    - Kadar lemak
                    - Kadar serat
                    - Kadar gula
                    - Kadar natrium
                    - Kadar kolesterol
                    - Kadar vitamin
                    
                    Tampilkan dalam format JSON. Contoh:
                    {
                        "kalori": 100,
                        "protein": 10,
                        "karbohidrat": 20,
                        "lemak": 5,
                        "serat": 3,
                        "gula": 2,
                        "natrium": 1,
                        "kolesterol": 0,
                        "vitamin": 0
                    }
                    
                    Pastikan untuk tidak memberikan penjelasan tambahan, hanya hasil JSON di atas.
                    Kalau bukan makanan, jangan tampilkan hasil JSON. Nullkan hasilnya.
                    Jika tidak bisa mendeteksi makanan, jangan tampilkan hasil JSON. Nullkan hasilnya.
                    
                    
                    Kalau misalnya kamu tidak tau isi makanan ini, dikira-kira saja.
                    Misalnya makanan kemasan, itu yang secara umum ada di dalamnya, dikira-kira saja.
                """.trimIndent()
            )
            .build()

        val answer = model.generateContent(content).text ?:
            "Failed to generate content"

        return@withContext answer
    }
}

//class CameraViewModel : ViewModel() {
//    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
//    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest
//
//    private val _lensFacing = MutableStateFlow(LENS_FACING_FRONT)
//    val lensFacing: StateFlow<Int> = _lensFacing
//
//    internal val cameraPreviewUseCase = Preview.Builder().build().apply {
//        setSurfaceProvider { newSurfaceRequest ->
//            _surfaceRequest.update { newSurfaceRequest }
//        }
//    }
//
//    fun toggleLensFacing() {
//        _lensFacing.value = if (_lensFacing.value == LENS_FACING_FRONT) {
//            LENS_FACING_BACK
//        } else {
//            LENS_FACING_FRONT
//        }
//    }
//
//    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
//        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
//        val cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(_lensFacing.value)
//            .build()
//
//        processCameraProvider.unbindAll()
//        processCameraProvider.bindToLifecycle(
//            lifecycleOwner, cameraSelector, cameraPreviewUseCase
//        )
//
//        try {
//            awaitCancellation()
//        } finally {
//            processCameraProvider.unbindAll()
//        }
//    }
//}