package com.example.giziku.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.giziku.model.AnakEntity
import com.example.giziku.util.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruLaporanGizianakScreen(anakId: String, userViewModel: UserViewModel, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var anak by remember { mutableStateOf<AnakEntity?>(null) }

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

    // Load data anak dari ViewModel
    LaunchedEffect(anakId) {
        anak = userViewModel.getAnakByKodeUnik(anakId)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) { // Kembali ke Home
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF127369)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Data Siswa", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        anak?.let {
            var selectedKelas by remember { mutableStateOf(it.kelas ?: kelasList.first()) }
            TextField(label = "Nama Lengkap", value = it.nama)
            TextField(label = "Tanggal Lahir", value = it.tanggalLahir ?: "-")
            TextField(label = "Jenis Kelamin", value = it.jenisKelamin ?: "-")
            TextField(label = "Kelas", value = it.kelas ?: "-")
        }?: Text("Memuat data...", modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(16.dp))

        Text("Grafik Perkembangan", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFFDFF5F4), shape = RoundedCornerShape(10.dp))
                .padding(8.dp)
        ) {
            Text("Tinggi Badan Sesuai Usia", modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun TextField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}