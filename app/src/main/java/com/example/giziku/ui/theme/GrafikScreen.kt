package com.example.giziku.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.giziku.R

@Composable
fun GrafikScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6F2))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tombol Back & Judul
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF127369)
                )
            }
            Text(text = "Grafik Anak", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(text = "Grafik Perkembangan", fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        GrafikItem(label = "Tinggi Badan Sesuai Usia", navController)
        GrafikItem(label = "Berat Badan Sesuai Usia", navController)
        GrafikItem(label = "IDEAL", navController)
    }
}

@Composable
fun GrafikItem(label: String, navController: NavController) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* TODO: Bisa diarahkan ke detail grafik */ },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF127369))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.graph_placeholder), // Ganti dengan gambar grafik
                contentDescription = label,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 8.dp)
            )
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}