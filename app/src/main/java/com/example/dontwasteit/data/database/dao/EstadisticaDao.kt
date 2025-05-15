package com.example.dontwasteit.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dontwasteit.data.database.entities.Estadistica


@Dao
interface EstadisticaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(estadistica: Estadistica)

    @Query("SELECT * FROM estadisticas WHERE fecha = :mes LIMIT 1")
    suspend fun getByMes(mes: String): Estadistica?

    @Query("DELETE FROM estadisticas")
    suspend fun deleteAll()
}
