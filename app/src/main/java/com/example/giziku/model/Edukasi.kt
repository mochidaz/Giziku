package com.example.giziku.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "edukasi",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class EdukasiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int, // ini harus sesuai dengan userTenagaMedisId di ProfileTenagaMedis
    val judul: String,
    val deskripsi: String,
    val fileUri: String
)


