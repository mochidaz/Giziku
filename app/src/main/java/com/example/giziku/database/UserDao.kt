package com.example.giziku.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.giziku.model.AnakEntity
import com.example.giziku.model.ProfileOrangTua
import com.example.giziku.model.ProfileTenagaMedis
import com.example.giziku.model.ProfileTenagaPendidikan
import com.example.giziku.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getUserById(id: kotlin.Long?): User?
}

@Dao
interface ProfileOrangTuaDao {

    @Insert
    suspend fun insertProfile(profile: ProfileOrangTua): Long

    @Update
    suspend fun updateProfile(profile: ProfileOrangTua)

    @Query("SELECT * FROM profileOrangTua WHERE userOrangTuaId = :userId")
    suspend fun getProfileByUserId(userId: Long): ProfileOrangTua?

    @Query("DELETE FROM profileOrangTua WHERE userOrangTuaId = :userId")
    suspend fun deleteProfileByUserId(userId: Long)

}

@Dao
interface ProfileTenagaPendidikanDao {

    @Insert
    suspend fun insertProfile(profile: ProfileTenagaPendidikan)

    @Update
    suspend fun updateProfile(profile: ProfileTenagaPendidikan)

    @Query("SELECT * FROM profileTenagaPendidikan WHERE userTenagaPendidikanId = :userId")
    suspend fun getProfileByUserId(userId: Long): ProfileTenagaPendidikan?

    @Query("DELETE FROM profileTenagaPendidikan WHERE userTenagaPendidikanId = :userId")
    suspend fun deleteProfileByUserId(userId: Long)

    @Query("SELECT * FROM anak WHERE kodeUnik = :kode")
    suspend fun getAnakByKode(kode: String): AnakEntity?

}

@Dao
interface ProfileTenagaMedisDao {

    @Insert
    suspend fun insertProfile(profile: ProfileTenagaMedis)

    @Update
    suspend fun updateProfile(profile: ProfileTenagaMedis)

    @Query("SELECT * FROM profileTenagaMedis WHERE userTenagaMedisId = :userId")
    suspend fun getProfileByUserId(userId: Long): ProfileTenagaMedis?

    @Query("DELETE FROM profileTenagaMedis WHERE userTenagaMedisId = :userId")
    suspend fun deleteProfileByUserId(userId: Long)
}


