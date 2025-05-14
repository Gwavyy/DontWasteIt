package com.example.dontwasteit.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dontwasteit.data.database.entities.Usuario

@Dao
interface UsuarioDao {
    @Insert suspend fun insert(usuario: Usuario): Long
    @Query("SELECT * FROM usuarios LIMIT 1") suspend fun getUsuario(): Usuario?
}
