package com.example.giziku.ui.theme

import android.app.Application
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.giziku.R
import com.example.giziku.model.ProfileOrangTua
import com.example.giziku.model.User
import com.example.giziku.util.UserViewModel
import com.example.giziku.util.UserViewModelFactory
import java.io.File

@Composable
fun OrangtuaProfileScreen(navController: NavController) {
    val context = LocalContext.current
    var profile by remember { mutableStateOf<ProfileOrangTua?>(null) }

    val application = context.applicationContext as Application
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(application))

    LaunchedEffect(Unit) {
        val currentUserId = userViewModel.getCurrentUserId()
        val fetchedProfile = userViewModel.getProfileOrangTuaByUserId(currentUserId)
        profile = fetchedProfile
        Log.d("OrangtuaHomeScreen", "Profile fetched successfully: ${fetchedProfile?.username}")
    }

    val painter = if (!profile?.fileUri.isNullOrEmpty()) {
        rememberAsyncImagePainter(model = File(profile!!.fileUri!!))
    } else {
        painterResource(id = R.drawable.profile_placeholder)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6F2))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
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
            Text(text = "Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        profile?.let {
            // Foto Profil
            Image(
                painter = painter,
                contentDescription = "Foto Profil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )


            Spacer(modifier = Modifier.height(24.dp))

            // Info
            ProfileItem(label = "Nama Lengkap", value = it.username)
            ProfileItem(label = "Email", value = it.email)
            ProfileItem(label = "Nomor Handphone", value = it.phone)
            ProfileItem(label = "Tanggal Lahir", value = it.tanggalLahir)
            ProfileItem(label = "Jenis Kelamin", value = it.jenisKelamin)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { navController.navigate("editprofileorangtua") },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF127369)),
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Edit", color = Color.White)
            }

            Button(
                onClick = { userViewModel.logout()  // Logout user
                    navController.navigate("awal") {  // Arahkan ke halaman login
                        popUpTo(0) { inclusive = true }  // Hapus semua halaman sebelumnya
                    }},
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF127369)),
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String, bordered: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (bordered) Modifier
                    .border(1.dp, Color(0xFF127369), RoundedCornerShape(4.dp))
                    .padding(8.dp) else Modifier
            )
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}