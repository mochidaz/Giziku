package com.example.giziku.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.giziku.model.AnakEntity
import com.example.giziku.model.EdukasiEntity
import com.example.giziku.model.ProfileOrangTua
import com.example.giziku.model.ProfileTenagaPendidikan
import com.example.giziku.model.ProfileTenagaMedis
import com.example.giziku.model.User

@Database(
    entities = [User::class, ProfileOrangTua::class, ProfileTenagaPendidikan::class, ProfileTenagaMedis::class
               , AnakEntity::class, EdukasiEntity::class],
    version = 25,  // Pastikan untuk memperbarui versi jika ada perubahan skema
    exportSchema = false  // Jika Anda tidak ingin mengekspor skema ke file
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun profileOrangTuaDao(): ProfileOrangTuaDao
    abstract fun profileTenagaPendidikanDao(): ProfileTenagaPendidikanDao
    abstract fun profileTenagaMedisDao(): ProfileTenagaMedisDao
    abstract fun anakDao(): AnakDao
    abstract fun edukasiDao(): EdukasiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "giziku_database"
                )
                    .fallbackToDestructiveMigration()  // Agar skema yang tidak cocok akan dihancurkan dan dibuat ulang
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
