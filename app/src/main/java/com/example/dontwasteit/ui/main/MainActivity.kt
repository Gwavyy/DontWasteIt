package com.example.dontwasteit.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dontwasteit.databinding.ActivityMainBinding
import com.example.dontwasteit.ui.addproduct.AddProductActivity
import com.example.dontwasteit.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ProductViewModel by viewModels {
        ProductViewModel.Factory
    }

    private var mostrandoConsumidos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView(mostrandoConsumidos)

        // Escuchar cambios en el switch
        binding.switchMostrarConsumidos.setOnCheckedChangeListener { _, isChecked ->
            mostrandoConsumidos = isChecked
            configurarRecyclerView(mostrandoConsumidos)
        }

        // Botón flotante para añadir productos
        binding.fabAddProducto.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarRecyclerView(consumidos: Boolean) {
        val adapter = ProductAdapter(mostrarBotones = !consumidos).apply {
            onConsumidoClick = { productoActualizado ->
                viewModel.insert(productoActualizado)
            }
            onEliminarClick = { productoAEliminar ->
                viewModel.delete(productoAEliminar)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            if (consumidos) {
                viewModel.productosConsumidos.collectLatest { productos ->
                    adapter.submitList(productos)
                }
            } else {
                viewModel.productosNoConsumidos.collectLatest { productos ->
                    adapter.submitList(productos)
                }
            }
        }
    }
}
