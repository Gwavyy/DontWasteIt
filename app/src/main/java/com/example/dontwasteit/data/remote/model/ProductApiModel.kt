package com.example.dontwasteit.data.remote.model

data class ProductResponse(
    val status: Int,
    val product: ProductData?
)

data class ProductData(
    val product_name: String?,
    val brands: String?,
    val categories: String?,
    val nutriscore_grade: String?,
    val image_front_url: String?,
    val quantity: String?
)
