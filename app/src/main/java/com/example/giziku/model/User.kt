package com.example.giziku.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val username: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String
)

@Entity(
    tableName = "profileOrangTua",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userOrangTuaId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["userOrangTuaId"])]
)
data class ProfileOrangTua(
    @PrimaryKey
    val userOrangTuaId: Long,
    val username: String,
    val email: String,
    val phone: String,
    val tanggalLahir: String,
    val jenisKelamin: String,
    val fileUri: String
)

@Entity(
    tableName = "profileTenagaPendidikan",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userTenagaPendidikanId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["userTenagaPendidikanId"])]
)
data class ProfileTenagaPendidikan(
    @PrimaryKey
    val userTenagaPendidikanId: Long,
    val username: String,
    val email: String,
    val phone: String,
    val mataPelajaran: String,
    val jenisKelamin: String,
    val fileUri: String
)

@Entity(
    tableName = "profileTenagaMedis",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userTenagaMedisId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["userTenagaMedisId"])]
)
data class ProfileTenagaMedis(
    @PrimaryKey
    val userTenagaMedisId: Long,
    val username: String,
    val email: String,
    val phone: String,
    val spesialis: String,
    val jenisKelamin: String,
    val fileUri: String
)
