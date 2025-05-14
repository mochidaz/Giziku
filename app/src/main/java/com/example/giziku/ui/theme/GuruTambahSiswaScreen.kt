package com.example.giziku.ui.theme

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.giziku.util.UserViewModel
import com.example.giziku.util.UserViewModelFactory
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme


@Composable
fun GuruTambahSiswaScreen(navController: NavController) {
    var idSiswa by remember { mutableStateOf(TextFieldValue("")) }
    val kelasList = listOf(
        "Kelas 1A",
        "Kelas 2A",
        "Kelas 3A",
        "Kelas 4A",
        "Kelas 5A",
        "Kelas 6A",
        "Kelas 1B",
        "Kelas 2B",
        "Kelas 3B",
        "Kelas 4B",
        "Kelas 5B",
        "Kelas 6B"
    )
    var selectedKelas by remember { mutableStateOf(kelasList.first()) }
    var snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val application = context.applicationContext as Application
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(application))

    var anakId by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf<String?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(anakId) {
        if (anakId.isNotBlank()) {
            val exist = userViewModel.isAnakAlreadyExist(anakId)
            if (exist) {
                showMessage = "Kode sudah digunakan!"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar sederhana
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Tambah Siswa", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = idSiswa,
            onValueChange = { idSiswa = it },
            placeholder = { Text("Masukkan ID") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF004D40),
                unfocusedBorderColor = Color(0xFF004D40)
            ),
            isError = errorMessage != null // This sets the error state of the text field
        )

        // Display error message below the text field
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall, // You can change the style if needed
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            border = BorderStroke(1.dp, Color(0xFF004D40)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Kelas", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                kelasList.forEach { kelas ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedKelas == kelas,
                            onClick = { selectedKelas = kelas },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF004D40)
                            )
                        )
                        Text(text = kelas)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    val kodeInput = idSiswa.text.trim()
                    // Cek apakah ID sudah terpakai
                    if (idSiswa.text.isBlank()) {
                        errorMessage = "ID tidak boleh kosong"
                    } else {
                        // Ambil anak berdasarkan kode unik (idSiswa)
                        val anak = userViewModel.getAnakByKodeUnik(idSiswa.text.trim())

                        if (anak != null) {
                            // Cek apakah anak sudah terdaftar di kelas
                            if (anak.kelas != null && anak.kelas.isNotEmpty()) {
                                // Jika anak sudah terdaftar di kelas, tampilkan error message
                                errorMessage = "ID sudah terdaftar di kelas ${anak.kelas}. Tidak bisa ditambahkan lagi."
                            } else {
                                // Jika anak belum terdaftar di kelas, lanjutkan dengan penambahan ke kelas baru
                                val updatedAnak = anak.copy(kelas = selectedKelas)
                                userViewModel.updateAnak(updatedAnak) {
                                    scope.launch {
                                        Toast.makeText(context, "Siswa berhasil ditambahkan ke kelas $selectedKelas", Toast.LENGTH_SHORT).show()
                                        navController.navigate("teacherHomeScreen") {
                                            popUpTo("tambahsiswa") { inclusive = true }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Jika ID tidak ditemukan, tampilkan error message
                            errorMessage = "Kode tidak ditemukan. Pastikan kode benar."
                        }
                    }

                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.height(40.dp)
        ) {
            Text("Tambah", color = Color.White)
        }
    }
}

