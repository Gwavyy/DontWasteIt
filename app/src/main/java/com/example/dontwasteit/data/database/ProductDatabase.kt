package com.example.dontwasteit.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dontwasteit.data.database.dao.ProductDao
import com.example.dontwasteit.data.database.entities.Product

@Database(
    entities = [Product::class],
    version = 4,
    exportSchema = false
)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
