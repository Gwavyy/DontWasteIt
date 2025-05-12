package com.example.dontwasteit.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dontwasteit.data.database.entities.Estadistica
import kotlinx.coroutines.flow.Flow

@Dao
interface EstadisticaDao {

    @Insert
    suspend fun insert(estadistica: Estadistica)

    @Query("SELECT * FROM estadisticas ORDER BY fecha DESC")
    fun getAll(): Flow<List<Estadistica>>

    @Query("DELETE FROM estadisticas")
    suspend fun deleteAll()
}
