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
import java.time.format.DateTimeFormatter

//ViewModel principal de la app, encargado de gestionar los productos
// Gestiona tambien las estadisticas del mes actual. Se comunica con los repositorios
class ProductViewModel(private val repository: ProductRepository, private val estadisticaRepository: EstadisticaRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    init {
        // Carga inicial de los producto
        loadProducts()
    }

    //Lanza una corrutina que recoge continuamente los productos
    // desde el repositorio y actualiza el estado interno.
    private fun loadProducts() {
        viewModelScope.launch {
            repository.allProducts.collect {
                _products.value = it
            }
        }
    }

    //Inserta un producto en la base de datos
    fun insert(product: Product) {
        viewModelScope.launch {
            repository.insert(product)
        }
    }

    //Elimina un producto de la base de datos
    fun delete(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }
    //Devuelve la estadística correspondiente al mes actual.
    suspend fun obtenerEstadisticaDelMes(): Estadistica? {
        val mes = LocalDate.now().toString().substring(0, 7)
        return estadisticaRepository.obtenerDelMes(mes)
    }

    //Actualiza la estadística mensual. Calcula cuantos productos han sido consumidos,
    //cuantos han caducado, el porcentaje de desperdicio y cual es la categoría más consumida.
    fun actualizarEstadisticaMensual() {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val hoy = LocalDate.now()
            val productos = repository.allProducts.first()
            val mes = hoy.toString().substring(0, 7)

            val productosDelMes = productos.filter {
                it.fechaRegistro.startsWith(mes)
            }

            val noConsumidos = productosDelMes.count {
                !it.consumido && try {
                    !LocalDate.parse(it.fechaCaducidad, formatter).isBefore(hoy)
                } catch (_: Exception) {
                    false
                }
            }

            val consumidos = productosDelMes.count { it.consumido }

            val caducados = productosDelMes.count {
                !it.consumido && try {
                    LocalDate.parse(it.fechaCaducidad, formatter).isBefore(hoy)
                } catch (_: Exception) {
                    false
                }
            }

            val total = productosDelMes.size
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

            productosDelMes.filter{
                !it.consumido && try {
                    val fecha = LocalDate.parse(it.fechaCaducidad, formatter)
                    fecha.isBefore(hoy) && !it.contadoEnEstadistica
                } catch (_: Exception) {
                    false
                }
            }.forEach { productoCaducado ->
                val actualizado = productoCaducado.copy(contadoEnEstadistica = true)
                repository.insert(actualizado)
            }
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
    //Para separar los productos segun su estado de consumo
    val productosNoConsumidos = products.map { lista ->
        lista.filter { !it.consumido }
    }

    val productosConsumidos = products.map { lista ->
        lista.filter { it.consumido }
    }
    //Al cambiar de mes borra todos los productos y estadísticas
    fun resetearMes() {
        viewModelScope.launch {
            repository.deleteAll()
            estadisticaRepository.borrarTodas()
        }
    }

}
