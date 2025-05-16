package com.example.dontwasteit.data.remote.model


/**
 * Representa la respuesta principal de la API de Open Food Facts.
 * Esta clase envuelve el objeto product y un status que indica si la busqueda fue exitosa.
 */
data class ProductResponse(
    val status: Int,
    val product: ProductData?
)
/**
 * Clase de datos que representa los detalles de un producto dentro de la respuesta JSON de la API.
 * Cada campo es nullable porque puede que no todos los productos tengan toda la informacion.
 */
data class ProductData(
    val product_name: String?,
    val brands: String?,
    val categories: String?,
    val nutriscore_grade: String?,
    val image_front_url: String?,
    val quantity: String?
)
