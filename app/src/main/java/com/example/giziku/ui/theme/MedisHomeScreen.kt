package com.example.giziku.ui.theme

import android.app.Application
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.giziku.R
import com.example.giziku.model.EdukasiEntity
import com.example.giziku.model.ProfileTenagaMedis
import com.example.giziku.util.UserViewModel
import com.example.giziku.util.UserViewModelFactory
import java.io.File


@Composable
fun MedisHomeScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(application))
    val userId = userViewModel.getCurrentUserId()

    // State untuk edukasi yang dipilih
    var selectedEdukasi by remember { mutableStateOf<EdukasiEntity?>(null) }

    if (selectedEdukasi == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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

            ProfileCard()

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.navigate("profile_medis") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF127369)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Profile", color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            MenuOptions(navController)

            // ✅ Perbaikan: tambahkan parameter onEdukasiClick
            EdukasiListSection(
                userId = userId,
                userViewModel = userViewModel,
                onEdukasiClick = { edukasi ->
                    selectedEdukasi = edukasi
                }
            )
        }
    } else {
        // ✅ Tampilkan detail edukasi jika salah satu diklik
        EdukasiDetailScreen(
            edukasi = selectedEdukasi!!,
            onBack = { selectedEdukasi = null }, // bukan navController.popBackStack()
            onDelete = {
                userViewModel.deleteEdukasi(it)
                selectedEdukasi = null // reset ke null agar kembali ke list
            }
        )


    }
}


@Composable
fun ProfileCard() {
    val context = LocalContext.current
    var profile by remember { mutableStateOf<ProfileTenagaMedis?>(null) }

    val application = context.applicationContext as Application
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(application))

    LaunchedEffect(Unit) {
        val currentUserId = userViewModel.getCurrentUserId()
        val fetchedProfile = userViewModel.getProfileTenagaMedisByUserId(currentUserId)
        profile = fetchedProfile
        Log.d("MedisHomeScreen", "Profile fetched successfully: ${fetchedProfile?.username}")
    }

    val painter = if (!profile?.fileUri.isNullOrEmpty()) {
        rememberAsyncImagePainter(model = File(profile!!.fileUri!!))
    } else {
        painterResource(id = R.drawable.profile_placeholder)
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            profile?.let {
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
                        fontSize = 14.sp,
                    )
                    Text(
                        text = "Spesialis: ${it.spesialis}",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp) // Tambah jarak dari atas
                    )
                    Text(
                        text = "Jenis Kelamin: ${it.jenisKelamin}",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }?: run {
                // Jika data profile belum ada, tampilkan teks loading atau placeholder
                Text(text = "Memuat profil...", fontSize = 14.sp)
            }

//            Column {
//                Text(text = "Nama Lengkap :", fontSize = 14.sp)
//                Text(text = "Dr. Susanti Himawa, Sp.A", fontSize = 16.sp, fontWeight = FontWeight.Bold)
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "Spesialis :", fontSize = 14.sp)
//                Text(text = "Spesialis Anak", fontSize = 16.sp, fontWeight = FontWeight.Bold)
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "Jenis Kelamin :", fontSize = 14.sp)
//                Text(text = "Perempuan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
//            }
        }
    }
}

@Composable
fun MenuOptions(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(application))

    // Mendapatkan userId dari ViewModel
    val userId = userViewModel.getCurrentUserId() // Pastikan metode ini tersedia di UserViewModel

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally)
        ) {
            MenuItemMedis(iconRes = R.drawable.ic_education, label = "Edukasi Gizi") {
                // Menavigasi ke halaman edukasi gizi dengan userId
                navController.navigate("edukasi_gizi/$userId")
            }
            MenuItemMedis(iconRes = R.drawable.ic_graph, label = "Grafik") {
                navController.navigate("grafik") // Navigasi ke halaman grafik
            }
        }
    }
}


@Composable
fun MenuItemMedis(iconRes: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() } // Agar bisa diklik
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun EdukasiListSection(
    userId: Long,
    userViewModel: UserViewModel,
    onEdukasiClick: (EdukasiEntity) -> Unit
) {
    val edukasiList by userViewModel.getEdukasiByUserId(userId).observeAsState(emptyList())

    Spacer(modifier = Modifier.height(32.dp))
    Text("Daftar Edukasi", fontWeight = FontWeight.Bold, fontSize = 18.sp)

    if (edukasiList.isEmpty()) {
        Text("Belum ada edukasi yang ditambahkan.")
    } else {
        edukasiList.forEach { edukasi ->
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(2.dp)
                    .clickable { onEdukasiClick(edukasi) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val imageFile = File(edukasi.fileUri)

                    if (imageFile.exists()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageFile),
                            contentDescription = "Gambar Edukasi",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = edukasi.judul,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = edukasi.deskripsi,
                            fontSize = 14.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EdukasiDetailScreen(
    edukasi: EdukasiEntity,
    onBack: () -> Unit,
    onDelete: (EdukasiEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi Penghapusan") },
            text = { Text("Apakah Anda yakin ingin menghapus edukasi ini?") },
            confirmButton = {
                Text(
                    "Hapus",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            showDialog = false
                            onDelete(edukasi)
                        },
                    color = Color.Red
                )
            },
            dismissButton = {
                Text(
                    "Batal",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { showDialog = false },
                    color = Color.Gray
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF127369))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                showDialog = true // 👈 Tampilkan dialog saat tombol hapus ditekan
            }) {
                Icon(imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = Color.Red)
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)


        Spacer(modifier = Modifier.height(16.dp))
        Text(text = edukasi.judul, fontSize = 25.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(18.dp))

        val imageFile = File(edukasi.fileUri)
        if (imageFile.exists()) {
            Image(
                painter = rememberAsyncImagePainter(model = imageFile),
                contentDescription = "Gambar Edukasi",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(text = edukasi.deskripsi, fontSize = 16.sp, textAlign = TextAlign.Justify)
    }
}

