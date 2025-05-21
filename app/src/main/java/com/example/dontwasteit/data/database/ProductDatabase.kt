package com.example.dontwasteit.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dontwasteit.data.database.dao.*
import com.example.dontwasteit.data.database.entities.*

//Se anotan las entidades que conforman las tablas de la base de datos y la version actual del esquema.
@Database(entities = [Product::class, Estadistica::class, Usuario::class, Categoria::class], version = 9, exportSchema = false)
abstract class ProductDatabase : RoomDatabase() {

    // Cada funcion abstracta devuelve una instancia del DAO correspondiente para acceder a cada tabla
    abstract fun productDao(): ProductDao
    abstract fun estadisticaDao(): EstadisticaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
}
