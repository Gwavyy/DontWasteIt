package com.example.dontwasteit.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val fechaCaducidad: String,
    val fechaRegistro: String,
    val escaneado: Boolean = false,
    val consumido: Boolean = false,
    val barcode: String? = null,
    val marca: String? = null,
    val categoria: String? = null,
    val nutriscore: String? = null,
    val imagenUrl: String? = null,
    val cantidad: String? = null
)

