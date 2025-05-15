package com.example.dontwasteit.data.repository

import com.example.dontwasteit.data.database.dao.EstadisticaDao
import com.example.dontwasteit.data.database.entities.Estadistica


class EstadisticaRepository(private val dao: EstadisticaDao) {

    suspend fun insertar(estadistica: Estadistica) = dao.insertOrUpdate(estadistica)

    suspend fun obtenerDelMes(mes: String): Estadistica? = dao.getByMes(mes)

    suspend fun borrarTodas() = dao.deleteAll()

}
