package com.example.giziku.ui.theme

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.giziku.model.AnakEntity
import com.example.giziku.util.UserViewModel
import com.example.giziku.util.UserViewModelFactory

@Composable
fun TeacherStudentListScreen(navController: NavController, kelas: String) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(application))

    var siswaList by remember { mutableStateOf<List<AnakEntity>>(emptyList()) }

    // Gunakan LaunchedEffect untuk memanggil suspend function dalam coroutine
    LaunchedEffect(kelas) {
        // Panggil suspend function getAnakByKelas di sini
        siswaList = userViewModel.getAnakByKelas(kelas)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
            Text(text = " $kelas ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)


        LazyColumn {
            items(siswaList) { siswa ->
                StudentCard(
                    name = siswa.nama,
                    birthDate = siswa.tanggalLahir,
                    gender = siswa.jenisKelamin,
                    onClick = {
                        navController.navigate("gurulaporangizianak/${siswa.kodeUnik}")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

    }
}

@Composable
fun StudentCard(
    name: String,
    birthDate: String,
    gender: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color(0xFF005F4B), shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            LabeledText(label = "Nama Lengkap", value = name)
            LabeledText(label = "Tanggal Lahir", value = birthDate)
            LabeledText(label = "Jenis Kelamin", value = gender)
        }
    }
}

@Composable
fun LabeledText(label: String, value: String) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$label : ")
            }
            append(value)
        },
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
