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
    private lateinit var adapter: ProductAdapter
    private val viewModel: ProductViewModel by viewModels {
        ProductViewModel.Factory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar RecyclerView
        adapter = ProductAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Observar datos desde ViewModel
        lifecycleScope.launch {
            viewModel.products.collectLatest { productos ->
                adapter.submitList(productos)
            }
        }

        // Botón flotante para añadir productos
        binding.fabAddProducto.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }
}
