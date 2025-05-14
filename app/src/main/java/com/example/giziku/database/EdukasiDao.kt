package com.example.giziku.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.giziku.model.EdukasiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EdukasiDao {
    @Insert
    suspend fun insertEdukasi(edukasi: EdukasiEntity)

    @Query("SELECT * FROM edukasi WHERE userId = :userId")
    fun getByUserId(userId: Long): LiveData<List<EdukasiEntity>> // Menggunakan LiveData untuk reactivity

    @Delete
    suspend fun deleteEdukasi(edukasi: EdukasiEntity)

    @Query("SELECT * FROM edukasi WHERE id = :id LIMIT 1")
    fun getEdukasiById(id: Long): EdukasiEntity?

    @Query("SELECT * FROM edukasi")
    fun getAllEdukasi(): LiveData<List<EdukasiEntity>>

    @Query("SELECT * FROM edukasi WHERE id = :id")
    fun getEdukasiById(id: Int): LiveData<EdukasiEntity?>


}
