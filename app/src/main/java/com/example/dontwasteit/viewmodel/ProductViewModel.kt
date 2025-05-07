package com.example.dontwasteit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.dontwasteit.DontWasteItApplication
import com.example.dontwasteit.data.database.entities.Product
import com.example.dontwasteit.data.repository.ProductRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

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

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as DontWasteItApplication
                ProductViewModel(app.repository)
            }
        }
    }
    val productosNoConsumidos = products.map { lista ->
        lista.filter { !it.consumido }
    }

    val productosConsumidos = products.map { lista ->
        lista.filter { it.consumido }
    }
}
