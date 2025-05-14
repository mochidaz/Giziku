package com.example.giziku.util

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.giziku.database.AppDatabase
import com.example.giziku.database.UserRepository
import com.example.giziku.model.AnakEntity
import com.example.giziku.model.EdukasiEntity
import com.example.giziku.model.ProfileOrangTua
import com.example.giziku.model.ProfileTenagaMedis
import com.example.giziku.model.ProfileTenagaPendidikan
import com.example.giziku.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val profileOrangTuaDao = AppDatabase.getDatabase(application).profileOrangTuaDao()
    private val profileTenagaPendidikanDao = AppDatabase.getDatabase(application).profileTenagaPendidikanDao()
    private val profileTenagaMedisDao = AppDatabase.getDatabase(application).profileTenagaMedisDao()
    private val anakDao = AppDatabase.getDatabase(application).anakDao()
    private val edukasiDao = AppDatabase.getDatabase(application).edukasiDao()

    init {
        userRepository = UserRepository(userDao, profileOrangTuaDao, profileTenagaPendidikanDao, profileTenagaMedisDao, anakDao, edukasiDao)
    }

    fun logout() {
        viewModelScope.launch {
            // Hanya hapus ID user, JANGAN hapus profil
            val sharedPrefs = getApplication<Application>().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().remove("currentUserId").apply()
        }
    }

    val currentUserId: Long?
        get() {
            val sharedPrefs = getApplication<Application>().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
            val id = sharedPrefs.getLong("currentUserId", -1L)
            return if (id != -1L) id else null
        }


    // Fungsi untuk register user
    fun registerUserDynamicRole(
        username: String, email: String, phone: String, password: String,
        role: String, tanggalLahir: String?, spesialis: String?, mataPelajaran: String?, jenisKelamin: String
    ) {
        val user = User(username = username, email = email, phone = phone, password = password, role = role)

        viewModelScope.launch {
            val userId = userRepository.insertUserAndGetId(user)

            when (role) {
                "Orang Tua" -> {
                    val profile = ProfileOrangTua(
                        userOrangTuaId = userId,
                        username = username,
                        email = email,
                        phone = phone,
                        tanggalLahir = tanggalLahir ?: "",
                        jenisKelamin = jenisKelamin,
                        fileUri = ""
                    )
                    userRepository.insertUserAndProfile(profile)
                }
                "Tenaga Kesehatan" -> {
                    val profile = ProfileTenagaMedis(
                        userTenagaMedisId = userId,
                        username = username,
                        email = email,
                        phone = phone,
                        spesialis = spesialis ?: "",
                        jenisKelamin = jenisKelamin,
                        fileUri = ""
                    )
                    userRepository.insertUserAndProfileTenagaMedis(profile)
                }
                "Tenaga Pendidikan" -> {
                    val profile = ProfileTenagaPendidikan(
                        userTenagaPendidikanId = userId,
                        username = username,
                        email = email,
                        phone = phone,
                        mataPelajaran = mataPelajaran ?: "",
                        jenisKelamin = jenisKelamin,
                        fileUri = ""
                    )
                    userRepository.insertUserAndProfileTenagaPendidikan(profile)
                }
            }
        }
    }

    // Fungsi untuk login user
    fun loginUser(email: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            if (user != null && user.password == password) {
                onResult(user)
            } else {
                onResult(null)
            }
        }
    }

    suspend fun getProfileOrangTuaByUserId(userId: Long): ProfileOrangTua? {
        return userRepository.getProfileOrangTuaByUserId(userId)
    }
    suspend fun getProfileTenagaPendidikanByUserId(userId: Long): ProfileTenagaPendidikan? {
        return userRepository.getProfileTenagaPendidikanByUserId(userId)
    }
    suspend fun getProfileTenagaMedisByUserId(userId: Long): ProfileTenagaMedis? {
        return userRepository.getProfileTenagaMedisByUserId(userId)
    }

    fun updateProfileOrangTua(profile: ProfileOrangTua) {
        viewModelScope.launch {
            userRepository.updateProfileOrangTua(profile)
        }
    }

    fun updateProfileTenagaPendidikan(profile: ProfileTenagaPendidikan) {
        viewModelScope.launch {
            userRepository.updateProfileTenagaPendidikan(profile)
        }
    }

    fun updateProfileTenagaMedis(profile: ProfileTenagaMedis) {
        viewModelScope.launch {
            userRepository.updateProfileTenagaMedis(profile)
        }
    }

    fun setCurrentUserId(userId: Long) {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putLong("currentUserId", userId).apply()
    }

    fun getCurrentUserId(): Long {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getLong("currentUserId", -1L)
    }

    fun insertAnak(anak: AnakEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            anakDao.insertAnak(anak)
            onSuccess()
        }
    }

    fun getAnakByOrangTuaId(orangTuaId: Long): List<AnakEntity> {
        return runBlocking {
            userRepository.getAnakByOrangTuaId(orangTuaId)
        }
    }

    fun deleteAnak(anakId: Int) {
        viewModelScope.launch {
            // Menjalankan operasi penghapusan di background thread
            userRepository.deleteAnakById(anakId)
        }
    }

    suspend fun getAnakById(id: Int): AnakEntity? {
        return userRepository.getAnakById(id)
    }

    suspend fun getAnakByKodeUnik(kode: String): AnakEntity? {
        return anakDao.getAnakByKodeUnik(kode)
    }


    fun updateAnak(anak: AnakEntity, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            userRepository.updateAnak(anak)
            onSuccess() // Callback success
        }
    }
    
    suspend fun getAnakByKelas(kelas: String): List<AnakEntity> {
        return userRepository.getAnakByKelas(kelas)
    }

    private val _selectedKelas = mutableStateOf<String?>(null)
    val selectedKelas: State<String?> get() = _selectedKelas

    fun setSelectedKelas(kelas: String) {
        _selectedKelas.value = kelas
    }

    suspend fun isAnakAlreadyExist(anakId: String): Boolean {
        return anakDao.getAnakById(anakId) != null
    }

    fun insertEdukasi(userId: Int, judul: String, deskripsi: String, fileUri: String) {
        viewModelScope.launch {
            userRepository.insertEdukasi(
                EdukasiEntity(
                    userId = userId,
                    judul = judul,
                    deskripsi = deskripsi,
                    fileUri = fileUri
                )
            )
        }
    }

    private val database = AppDatabase.getDatabase(application)

    // Ambil data edukasi sebagai LiveData
    fun getEdukasiByUserId(userId: Long): LiveData<List<EdukasiEntity>> {
        return userRepository.getEdukasiByUserId(userId)
    }

    fun deleteEdukasi(edukasi: EdukasiEntity) {
        viewModelScope.launch {
            edukasiDao.deleteEdukasi(edukasi)
        }
    }

    fun getAllEdukasi(): LiveData<List<EdukasiEntity>> {
        return userRepository.getAllEdukasi()
    }
    fun getEdukasiById(id: Int): LiveData<EdukasiEntity?> {
        return userRepository.getEdukasiById(id)
    }
    suspend fun isEmailRegistered(email: String): Boolean {
        return userRepository.getUserByEmail(email) != null
    }
    suspend fun getProfileAnakByUserId(userId: Long): AnakEntity? {
        return anakDao.getAnakByUserId(userId)
    }
    fun updateAnak(anak: AnakEntity) {
        viewModelScope.launch {
            userRepository.updateAnak(anak)
        }
    }
}

