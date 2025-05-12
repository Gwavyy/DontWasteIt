package com.example.dontwasteit.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dontwasteit.data.database.dao.EstadisticaDao
import com.example.dontwasteit.data.database.dao.ProductDao
import com.example.dontwasteit.data.database.entities.Estadistica
import com.example.dontwasteit.data.database.entities.Product

@Database(
    entities = [Product::class, Estadistica::class],
    version = 5,
    exportSchema = false
)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun estadisticaDao(): EstadisticaDao
}


