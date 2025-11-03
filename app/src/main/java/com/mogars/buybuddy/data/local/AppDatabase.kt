package com.mogars.buybuddy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mogars.buybuddy.data.local.dao.ProductoDao
import com.mogars.buybuddy.data.local.dao.PrecioDao
import com.mogars.buybuddy.data.local.entity.ProductoEntity
import com.mogars.buybuddy.data.local.entity.PrecioEntity

@Database(
    entities = [ProductoEntity::class, PrecioEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun precioDao(): PrecioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "buybuddy_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}