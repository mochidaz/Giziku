package com.example.giziku

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.giziku.ui.theme.Awal
import com.example.giziku.ui.theme.DetailAnakScreen
import com.example.giziku.ui.theme.EditAnakScreen
import com.example.giziku.ui.theme.EditProfileScreen
import com.example.giziku.ui.theme.EdukasiDetailScreenOrangTua
import com.example.giziku.ui.theme.EdukasiGiziMedis
import com.example.giziku.ui.theme.GrafikScreen
import com.example.giziku.ui.theme.GuruLaporanGizianakScreen
import com.example.giziku.ui.theme.GuruTambahSiswaScreen
import com.example.giziku.ui.theme.Login
import com.example.giziku.ui.theme.MedisHomeScreen
import com.example.giziku.ui.theme.OrangtuaEditProfileScreen
import com.example.giziku.ui.theme.OrangtuaHomeScreen
import com.example.giziku.ui.theme.OrangtuaPendaftarananakScreen
import com.example.giziku.ui.theme.OrangtuaProfileScreen
import com.example.giziku.ui.theme.ProfileMedis
import com.example.giziku.ui.theme.Register
import com.example.giziku.ui.theme.TeacherHomeScreen
import com.example.giziku.ui.theme.TeacherProfileEditScreen
import com.example.giziku.ui.theme.TeacherProfileScreen
import com.example.giziku.ui.theme.TeacherStudentListScreen
import com.example.giziku.util.UserViewModel
import com.example.giziku.util.UserViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val application = applicationContext as Application
            val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(application))


            NavHost(navController, startDestination = "awal") {
                composable("awal") { Awal(navController) }
                composable("login") { Login(navController) }
                composable("register") { Register(navController) }

                composable("teacherhomescreen") { TeacherHomeScreen(navController) }
                composable("teacherprofilescreen") { TeacherProfileScreen(navController) }
                composable("teacherprofileeditscreen") { TeacherProfileEditScreen(navController) }

                composable("home") { MedisHomeScreen(navController) } // Pastikan ini benar
                composable("edit_profile") { EditProfileScreen(navController) }
                composable("profile_medis") { ProfileMedis(navController) }
                composable("grafik") { GrafikScreen(navController) }
                composable("tambahsiswa") { GuruTambahSiswaScreen(navController) }
                composable("edukasi_gizi/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: 0
                    EdukasiGiziMedis(navController, userId)
                }
                composable("edukasidetail/{edukasiId}") { backStackEntry ->
                    val edukasiId = backStackEntry.arguments?.getString("edukasiId")?.toIntOrNull()
                    val userViewModel: UserViewModel = viewModel(
                        factory = UserViewModelFactory(LocalContext.current.applicationContext as Application)
                    )

                    val edukasi by userViewModel.getEdukasiById(edukasiId ?: -1).observeAsState()

                    edukasi?.let {
                        EdukasiDetailScreenOrangTua(
                            edukasi = it,
                            onBack = { navController.popBackStack() }
                        )
                    } ?: Text("Edukasi tidak ditemukan")
                }

                composable(
                    route = "gurulaporangizianak/{anakId}",
                    arguments = listOf(navArgument("anakId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val anakId = backStackEntry.arguments?.getString("anakId") ?: ""
                    GuruLaporanGizianakScreen(anakId = anakId, userViewModel = userViewModel, navController)
                }

                composable("homeorangtua") { OrangtuaHomeScreen(navController) } // Pastikan ini benar
                composable("pendaftaran") { OrangtuaPendaftarananakScreen(navController) } // Pastikan ini benar
                composable("profileorangtua") { OrangtuaProfileScreen(navController) } // Pastikan ini benar
                composable("editprofileorangtua") { OrangtuaEditProfileScreen(navController) } // Pastikan ini benar
                composable("detailAnak") { OrangtuaEditProfileScreen(navController) } // Pastikan ini benar

                composable("detailanak/{id}") { backStackEntry ->
                    val anakId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
                    DetailAnakScreen(
                        anakId = anakId,
                        userViewModel = viewModel(), // atau pakai remember / inject sesuai kebutuhanmu
                        navController = navController, // Menambahkan navController di sini
                        onBack = { navController.popBackStack() }
                    )
                }
                composable("editAnak/{anakId}") { backStackEntry ->
                    val anakId = backStackEntry.arguments?.getString("anakId")?.toIntOrNull()
                    if (anakId != null) {
                        EditAnakScreen(
                            anakId = anakId,
                            navController = navController,
                            userViewModel = userViewModel
                        )
                    }
                }

                composable(
                    "teacherstudentlistscreen/{kelas}",
                    arguments = listOf(navArgument("kelas") { type = NavType.StringType })
                ) { backStackEntry ->
                    val kelas = backStackEntry.arguments?.getString("kelas") ?: ""
                    TeacherStudentListScreen(navController, kelas)
                }


            }
        }
    }
}
