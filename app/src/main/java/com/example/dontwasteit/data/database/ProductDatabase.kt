package com.example.dontwasteit.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dontwasteit.data.database.dao.*
import com.example.dontwasteit.data.database.entities.*

@Database(entities = [Product::class, Estadistica::class, Usuario::class, Categoria::class], version = 5, exportSchema = false)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun estadisticaDao(): EstadisticaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
}
