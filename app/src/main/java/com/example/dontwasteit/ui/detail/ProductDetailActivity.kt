package com.example.dontwasteit.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dontwasteit.databinding.ActivityProductDetailBinding


class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombre = intent.getStringExtra("nombre")
        val marca = intent.getStringExtra("marca")
        val nutriscore = intent.getStringExtra("nutriscore")
        val cantidad = intent.getStringExtra("cantidad")
        val categoria = intent.getStringExtra("categoria")
        val imagenUrl = intent.getStringExtra("imagenUrl")
        val fechaCaducidad = intent.getStringExtra("fechaCaducidad")

        binding.textNombre.text = nombre ?: "Producto"
        binding.textMarca.text = "Marca: ${marca ?: "-"}"
        binding.textNutriscore.text = "Nutriscore: ${nutriscore ?: "-"}"
        binding.textCantidad.text = "Cantidad: ${cantidad ?: "-"}"
        binding.textCategoria.text = "Categoría: ${categoria ?: "-"}"
        binding.textCaducidad.text = "Caduca: ${fechaCaducidad ?: "-"}"

        Glide.with(this)
            .load(imagenUrl)
            .into(binding.imageViewProducto)


    }
}
