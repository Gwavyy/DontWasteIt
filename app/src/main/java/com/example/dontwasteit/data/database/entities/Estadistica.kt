package com.example.dontwasteit.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estadisticas")
data class Estadistica(
    @PrimaryKey val fecha: String, // Ej: "2025-05"
    val productosConsumidos: Int,
    val productosDesechados: Int,
    val porcentajeDesechos: Float,
    val categoriaMasConsumida: String?,
    val productosNoConsumidos: Int
)
