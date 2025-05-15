package com.example.dontwasteit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.dontwasteit.DontWasteItApplication
import com.example.dontwasteit.data.database.entities.Estadistica
import com.example.dontwasteit.data.database.entities.Product
import com.example.dontwasteit.data.repository.EstadisticaRepository
import com.example.dontwasteit.data.repository.ProductRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProductViewModel(private val repository: ProductRepository, private val estadisticaRepository: EstadisticaRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.allProducts.collect {
                _products.value = it
            }
        }
    }

    fun insert(product: Product) {
        viewModelScope.launch {
            repository.insert(product)
        }
    }

    fun delete(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }
    suspend fun obtenerEstadisticaDelMes(): Estadistica? {
        val mes = LocalDate.now().toString().substring(0, 7)
        return estadisticaRepository.obtenerDelMes(mes)
    }


    fun actualizarEstadisticaMensual() {
        viewModelScope.launch {
            val productos = repository.allProducts.first()
            val mes = LocalDate.now().toString().substring(0, 7)

            val productosDelMes = productos.filter {
                it.fechaRegistro.startsWith(mes)
            }
            val noConsumidos = productosDelMes.filter {
                !it.consumido && try {
                    !LocalDate.parse(it.fechaCaducidad).isBefore(LocalDate.now())
                } catch (_: Exception) {
                    false
                }
            }.size

            val total = productosDelMes.size
            val consumidos = productosDelMes.count { it.consumido }
            val caducados = productosDelMes.count {
                !it.consumido && try {
                    LocalDate.parse(it.fechaCaducidad).isBefore(LocalDate.now())
                } catch (e: Exception) {
                    false
                }
            }

            val porcentaje = if (total > 0) (caducados.toFloat() / total) * 100 else 0f

            val categoriaMasConsumida = productosDelMes.filter { it.consumido && it.categoria != null }
                .groupingBy { it.categoria }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key

            val estadistica = Estadistica(
                fecha = mes,
                productosConsumidos = consumidos,
                productosDesechados = caducados,
                porcentajeDesechos = porcentaje,
                categoriaMasConsumida = categoriaMasConsumida,
                productosNoConsumidos = noConsumidos
            )

            estadisticaRepository.insertar(estadistica)
        }
    }



    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as DontWasteItApplication
                ProductViewModel(app.repository, app.estadisticaRepository)
            }
        }
    }
    val productosNoConsumidos = products.map { lista ->
        lista.filter { !it.consumido }
    }

    val productosConsumidos = products.map { lista ->
        lista.filter { it.consumido }
    }
    fun resetearMes() {
        viewModelScope.launch {
            repository.deleteAll()
            estadisticaRepository.borrarTodas()
        }
    }

}
