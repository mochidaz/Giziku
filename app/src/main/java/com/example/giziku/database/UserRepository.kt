package com.example.giziku.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.example.giziku.model.AnakEntity
import com.example.giziku.model.EdukasiEntity
import com.example.giziku.model.ProfileOrangTua
import com.example.giziku.model.ProfileTenagaMedis
import com.example.giziku.model.ProfileTenagaPendidikan
import com.example.giziku.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao,
    private val profileOrangTuaDao: ProfileOrangTuaDao,
    private val profileTenagaPendidikanDao: ProfileTenagaPendidikanDao,
    private val profileTenagaMedisDao: ProfileTenagaMedisDao,
    private val anakDao: AnakDao,
    private val edukasiDao: EdukasiDao
) {
    @Transaction
    suspend fun insertUserAndProfile(profile: ProfileOrangTua) {
        profileOrangTuaDao.insertProfile(profile)
    }

    @Transaction
    suspend fun insertUserAndProfileTenagaPendidikan(profile: ProfileTenagaPendidikan) {
        profileTenagaPendidikanDao.insertProfile(profile)
    }

    @Transaction
    suspend fun insertUserAndProfileTenagaMedis(profile: ProfileTenagaMedis) {
        profileTenagaMedisDao.insertProfile(profile)
    }

//    @Transaction
//    suspend fun insertUserAndProfileTenagaPendidikan(user: User, profile: ProfileTenagaPendidikan) {
//        val userId = userDao.insertUser(user)
//        val profileWithUserId = profile.copy(userTenagaPendidikanId = userId)
//        profileTenagaPendidikanDao.insertProfile(profileWithUserId)
//    }
//
//    @Transaction
//    suspend fun insertUserAndProfileTenagaMedis(user: User, profile: ProfileTenagaMedis) {
//        val userId = userDao.insertUser(user)
//        val profileWithUserId = profile.copy(userTenagaMedisId = userId)
//        profileTenagaMedisDao.insertProfile(profileWithUserId)
//    }


    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun updateProfileOrangTua(profile: ProfileOrangTua) {
        profileOrangTuaDao.updateProfile(profile)
    }

    suspend fun updateProfileTenagaMedis(profile: ProfileTenagaMedis) {
        profileTenagaMedisDao.updateProfile(profile)
    }

    suspend fun updateProfileTenagaPendidikan(profile: ProfileTenagaPendidikan) {
        profileTenagaPendidikanDao.updateProfile(profile)
    }

    suspend fun getProfileOrangTuaByUserId(userId: Long): ProfileOrangTua? {
        return profileOrangTuaDao.getProfileByUserId(userId)?.also {
            Log.d("UserRepository", "Profile fetched: ${it.username}")
        }
    }


    suspend fun getProfileTenagaPendidikanByUserId(userId: Long): ProfileTenagaPendidikan? {
        return profileTenagaPendidikanDao.getProfileByUserId(userId)?.also {
            Log.d("UserRepository", "Profile fetched: ${it.username}")
        }
    }

    suspend fun getProfileTenagaMedisByUserId(userId: Long): ProfileTenagaMedis? {
        return profileTenagaMedisDao.getProfileByUserId(userId)?.also {
            Log.d("UserRepository", "Profile fetched: ${it.username}")
        }
    }

    suspend fun insertUserAndGetId(user: User): Long {
        return userDao.insertUser(user) // pastikan DAO insertUser return Long
    }

    suspend fun getAnakByOrangTuaId(orangTuaId: Long): List<AnakEntity> {
        return anakDao.getAnakByOrangTuaId(orangTuaId)
    }

    suspend fun getAnakById(id: Int): AnakEntity? {
        return anakDao.getAnakById(id)
    }

    suspend fun deleteAnakById(anakId: Int) {
        anakDao.deleteAnakById(anakId)
    }

    suspend fun insertAnakKeKelas(anak: AnakEntity) {
        anakDao.insertAnakKeKelas(anak)
    }

    suspend fun getAnakByKelas(kelas: String): List<AnakEntity> {
        return anakDao.getAnakByKelas(kelas)
    }

    suspend fun isAnakAlreadyExist(anakId: String): Boolean {
        return anakDao.getAnakById(anakId) != null
    }

    suspend fun getAnakById(id: String): AnakEntity? {
        return anakDao.getAnakById(id)
    }

    suspend fun updateAnak(anak: AnakEntity) {
        anakDao.updateAnak(anak)
    }
    suspend fun getProfileAnakById(userId: Long): AnakEntity? {
        return anakDao.getAnakByUserId(userId)
    }

    suspend fun insertEdukasi(edukasi: EdukasiEntity) {
        edukasiDao.insertEdukasi(edukasi)
    }

    fun getEdukasiByUserId(userId: Long): LiveData<List<EdukasiEntity>> {
        return edukasiDao.getByUserId(userId)
    }

    fun getAllEdukasi(): LiveData<List<EdukasiEntity>> {
        return edukasiDao.getAllEdukasi()
    }

    fun getEdukasiById(id: Int): LiveData<EdukasiEntity?> {
        return edukasiDao.getEdukasiById(id)
    }

}
