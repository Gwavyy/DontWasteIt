package com.example.dontwasteit.data.remote.api

import com.example.dontwasteit.data.remote.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {

    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): ProductResponse
}
