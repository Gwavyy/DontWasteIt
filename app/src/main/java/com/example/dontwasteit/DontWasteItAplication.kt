package com.example.dontwasteit

import android.app.Application
import com.example.dontwasteit.data.database.provider.DatabaseProvider
import com.example.dontwasteit.data.repository.EstadisticaRepository
import com.example.dontwasteit.data.repository.ProductRepository

//Clase que se ejecuta antes que cualquier Activity o Service cuando la app arranca
//Se inicializan las dependencias globales que estarán disponibles en toda la app.
class DontWasteItApplication : Application() {
    //Instancia unica de la base de datos usando el proveedor personalizado
    val database by lazy { DatabaseProvider.provideDatabase(this) }
    //Repositorio de productos conectado al DAO de productos
    val repository by lazy { ProductRepository(database.productDao()) }
    //Repositorio de estadisticas conectado al DAO de estadisticas
    val estadisticaRepository by lazy { EstadisticaRepository(database.estadisticaDao()) }

}
