package com.example.giziku.ui.theme

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.giziku.util.UserViewModel
import com.example.giziku.util.UserViewModelFactory
import java.io.File
import java.io.FileOutputStream

@Composable
fun EdukasiGiziMedis(
    navController: NavController, userId: Int
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(application))

    val judul = remember { mutableStateOf("") }
    val deskripsi = remember { mutableStateOf("") }
    val fileUri = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileUri.value = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6F2))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bar atas
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Edukasi Gizi", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = judul.value,
            onValueChange = { judul.value = it },
            label = { Text("Judul") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = deskripsi.value,
            onValueChange = { deskripsi.value = it },
            label = { Text("Deskripsi") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fileUri.value?.toString() ?: "",
            onValueChange = {},
            label = { Text("File") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB4CDE6))
        ) {
            Text("Pilih Gambar dari Galeri", color = Color.Black)
        }

        // Preview gambar yang dipilih
        fileUri.value?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Preview Gambar",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val selectedUri = fileUri.value
                if (selectedUri != null) {
                    val copiedPath = copyImageToInternalStorage(
                        context,
                        selectedUri,
                        "edukasi_${System.currentTimeMillis()}.jpg"
                    )

                    if (copiedPath != null) {
                        userViewModel.insertEdukasi(
                            userId = userId,
                            judul = judul.value,
                            deskripsi = deskripsi.value,
                            fileUri = copiedPath
                        )
                        navController.popBackStack()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF127369))
        ) {
            Text("Simpan", color = Color.White)
        }
    }
}

// Fungsi untuk menyalin gambar ke internal storage
fun copyImageToInternalStorage(context: Context, uri: Uri, filename: String): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, filename)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
