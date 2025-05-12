package com.example.dontwasteit.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estadisticas")
data class Estadistica(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fecha: String,
    val productosConsumidos: Int,
    val productosDesechados: Int,
    val porcentajeDesechos: Float
)
