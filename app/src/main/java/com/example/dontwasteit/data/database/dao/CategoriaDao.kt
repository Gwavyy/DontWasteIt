package com.example.dontwasteit.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dontwasteit.data.database.entities.Categoria

@Dao
interface CategoriaDao {
    @Insert suspend fun insertAll(categorias: List<Categoria>)
    @Query("SELECT * FROM categorias") suspend fun getAll(): List<Categoria>
}
