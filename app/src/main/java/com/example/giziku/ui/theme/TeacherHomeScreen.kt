package com.example.giziku.ui.theme

import android.app.Application
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import com.example.giziku.R
import com.example.giziku.model.AnakEntity
import com.example.giziku.model.ProfileTenagaPendidikan
import com.example.giziku.util.UserViewModel
import com.example.giziku.util.UserViewModelFactory
import java.io.File

@Composable
fun TeacherHomeScreen(navController: NavController) {
    val context = LocalContext.current
    var profile by remember { mutableStateOf<ProfileTenagaPendidikan?>(null) }
    var siswaDiKelas by remember { mutableStateOf<List<AnakEntity>>(emptyList()) }

    val application = context.applicationContext as Application
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(application))

    // Menggunakan LaunchedEffect untuk menjalankan suspend function dalam coroutine
    LaunchedEffect(Unit) {
        // Memanggil getAnakByKelas dalam coroutine
        siswaDiKelas = userViewModel.getAnakByKelas("1A") // misalnya kelas 1A
        val currentUserId = userViewModel.getCurrentUserId()
        val fetchedProfile = userViewModel.getProfileTenagaPendidikanByUserId(currentUserId)
        profile = fetchedProfile
        Log.d("TeacherHomeScreen", "Profile fetched successfully: ${fetchedProfile?.username}")
    }

    val painter = if (!profile?.fileUri.isNullOrEmpty()) {
        rememberAsyncImagePainter(model = File(profile!!.fileUri!!))
    } else {
        painterResource(id = R.drawable.profile_placeholder)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Tambahkan scroll
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Welcome\nTo\nGiziKu!",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 56.sp,
            fontFamily = FontFamily.SansSerif,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))


        profile?.let {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        // Foto Profil
                    Image(
                        painter = painter,
                        contentDescription = "Foto Profil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Nama Lengkap: ${it.username}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(text = "Tanggal Lahir: ${it.mataPelajaran}", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        Text(text = "Jenis Kelamin: ${it.jenisKelamin}", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        } ?: run {
            // Jika data profile belum ada, tampilkan teks loading atau placeholder
            Text(text = "Memuat profil...", fontSize = 14.sp)
        }

//        ProfileCard(
//            name = "Himawa, S.pd",
//            subject = "Matematika",
//            gender = "Perempuan"
//        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.navigate("tambahsiswa") }, // ganti dengan rute yang sesuai
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(
                    text = "Tambah Siswa",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { navController.navigate("teacherprofilescreen") },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Text(
                    text = "Profile",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

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

        kelasList.forEach { kelas ->
            OutlinedButton(
                onClick = { navController.navigate("teacherstudentlistscreen/${kelas}") },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF004D40)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = kelas,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF004D40)
                )
            }
        }
    }
}

@Composable
fun ProfileCard(name: String, subject: String, gender: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, Color(0xFF004D40)),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Nama Lengkap :", fontWeight = FontWeight.SemiBold)
                Text(name)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Mata Pelajaran:", fontWeight = FontWeight.SemiBold)
                Text(subject)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Jenis Kelamin :", fontWeight = FontWeight.SemiBold)
                Text(gender)
            }
        }
    }
}
