package com.example.giziku.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "anak",
    foreignKeys = [ForeignKey(
        entity = ProfileOrangTua::class,
        parentColumns = ["userOrangTuaId"],
        childColumns = ["orangTuaId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AnakEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val tanggalLahir: String,
    val jenisKelamin: String,
    val beratBadan: String,
    val tinggiBadan: String,
    val kodeUnik: String,
    val orangTuaId: Long,
    val kelas: String? = null,
    val fileUri: String
)

