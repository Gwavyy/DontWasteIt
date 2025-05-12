package com.example.dontwasteit.data.repository

import com.example.dontwasteit.data.database.dao.EstadisticaDao
import com.example.dontwasteit.data.database.entities.Estadistica
import kotlinx.coroutines.flow.Flow

class EstadisticaRepository(private val dao: EstadisticaDao) {

    suspend fun insertar(estadistica: Estadistica) = dao.insert(estadistica)

    fun obtenerTodas(): Flow<List<Estadistica>> = dao.getAll()
}
