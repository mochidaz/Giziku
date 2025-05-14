package com.example.giziku.ui.theme

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.giziku.R
import com.example.giziku.model.ProfileTenagaMedis
import com.example.giziku.util.UserViewModel
import com.example.giziku.util.UserViewModelFactory
import java.io.File
import java.io.FileOutputStream

@Composable
fun EditProfileScreen(navController: NavController) {
    var selectedGender by remember { mutableStateOf("Perempuan")}

    val context = LocalContext.current
    val application = context.applicationContext as Application
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(application))

    var namaLengkap by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var spesialis by remember { mutableStateOf("") }

    val userId = remember { mutableStateOf<Long?>(null) }
    val fileUri = remember { mutableStateOf<Uri?>(null) }

// ambil userId dari sesi login
    var profile by remember { mutableStateOf<ProfileTenagaMedis?>(null) }

    LaunchedEffect(Unit) {
        val currentUserId = userViewModel.getCurrentUserId()
        userId.value = currentUserId // simpan userId ke state
        val fetchedProfile = userViewModel.getProfileTenagaMedisByUserId(currentUserId)
        profile = fetchedProfile
        fetchedProfile?.let {
            namaLengkap = it.username
            spesialis = it.spesialis
            email = it.email
            phone = it.phone
            selectedGender = it.jenisKelamin
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileUri.value = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6F2))
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tombol Back & Title
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

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Foto Profil
        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            // Jika ada gambar baru yang dipilih, tampilkan gambar itu
            if (fileUri.value != null) {
                Image(
                    painter = rememberImagePainter(fileUri.value),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape), // Memastikan gambar berbentuk lingkaran
                    contentScale = ContentScale.Crop // Memastikan gambar memenuhi lingkaran tanpa melampaui
                )
            } else {
                // Jika belum ada gambar baru, tampilkan gambar placeholder
                Image(
                    painter = painterResource(id = R.drawable.profile_placeholder),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape), // Memastikan gambar berbentuk lingkaran
                    contentScale = ContentScale.Crop // Memastikan gambar memenuhi lingkaran tanpa melampaui
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Input Fields
        OutlinedTextField(
            value = namaLengkap,
            onValueChange = { namaLengkap = it },
            label = { Text("Nama Lengkap") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = false
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("No. Telepon") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = false
        )

        OutlinedTextField(
            value = spesialis,
            onValueChange = { spesialis = it },
            label = { Text("Spesialis") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Jenis Kelamin
        Text(
            text = "Jenis Kelamin",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF127369)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GenderRadioOption(
                label = "Perempuan",
                selected = selectedGender == "Perempuan",
                onSelect = { selectedGender = it }
            )
            GenderRadioOption(
                label = "Laki-laki",
                selected = selectedGender == "Laki-laki",
                onSelect = { selectedGender = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Simpan
        Button(
            onClick = {
                val selectedUri = fileUri.value
                val copiedPath = selectedUri?.let {
                    copyImageToInternalStorageMedis(
                        context,
                        it,
                        "medis_${System.currentTimeMillis()}.jpg"
                    )
                }

                if (profile != null) {
                    val updatedProfile = ProfileTenagaMedis(
                        userTenagaMedisId = profile!!.userTenagaMedisId,
                        username = namaLengkap,
                        email = email,
                        phone = phone,
                        jenisKelamin = selectedGender,
                        spesialis = spesialis,
                        fileUri = copiedPath ?: profile!!.fileUri // pakai yang baru jika ada, kalau tidak pakai lama
                    )

                    userViewModel.updateProfileTenagaMedis(updatedProfile)
                    navController.popBackStack()
                }

            }, // Simpan dan kembali ke Home
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF127369)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(text = "Simpan", color = Color.White, fontSize = 16.sp)
        }
    }
}

fun copyImageToInternalStorageMedis(context: Context, uri: Uri, filename: String): String? {
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
@Composable
fun GenderRadioOption(label: String, selected: Boolean, onSelect: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = { onSelect(label) },
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF127369)
            )
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (selected) Color(0xFF127369) else Color.Black
        )
    }
}