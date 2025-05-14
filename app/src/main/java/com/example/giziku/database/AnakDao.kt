package com.example.giziku.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.giziku.model.AnakEntity

@Dao
interface AnakDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAnak(anak: AnakEntity)

    @Query("SELECT * FROM anak WHERE orangTuaId = :orangTuaId")
    suspend fun getAnakByOrangTuaId(orangTuaId: Long): List<AnakEntity>

    @Query("SELECT * FROM anak WHERE id = :id")
    suspend fun getAnakById(id: Int): AnakEntity?

    @Delete
    suspend fun deleteAnak(anak: AnakEntity)

    @Query("DELETE FROM anak WHERE id = :anakId")
    suspend fun deleteAnakById(anakId: Int)

    @Query("SELECT * FROM anak WHERE kodeUnik = :kode LIMIT 1")
    suspend fun getAnakByKodeUnik(kode: String): AnakEntity?

    @Update
    suspend fun updateAnak(anak: AnakEntity)

    @Query("SELECT * FROM anak WHERE kelas = :kelas")
    suspend fun getAnakByKelas(kelas: String): List<AnakEntity>

    @Query("SELECT * FROM anak WHERE id = :id LIMIT 1")
    suspend fun getAnakById(id: String): AnakEntity?


    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAnakKeKelas(anak: AnakEntity)

    @Query("SELECT * FROM anak WHERE orangTuaId = :userId LIMIT 1")
    suspend fun getAnakByUserId(userId: Long): AnakEntity?

}