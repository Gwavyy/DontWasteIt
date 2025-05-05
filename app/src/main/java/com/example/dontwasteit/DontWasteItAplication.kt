package com.example.dontwasteit

import android.app.Application
import com.example.dontwasteit.data.database.provider.DatabaseProvider
import com.example.dontwasteit.data.repository.ProductRepository

class DontWasteItApplication : Application() {
    val database by lazy { DatabaseProvider.provideDatabase(this) }
    val repository by lazy { ProductRepository(database.productDao()) }
}
