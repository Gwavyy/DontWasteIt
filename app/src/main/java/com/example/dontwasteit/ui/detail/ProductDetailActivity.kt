package com.example.dontwasteit.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dontwasteit.R
import com.example.dontwasteit.databinding.ActivityProductDetailBinding

/**
 * Muestra los detalles de un producto cuando el usuario pulsa sobre el desde la lista.
 * Muestra imagen, nombre, marca, cantidad, categoria, nutriscore y fecha de caducidad.
 */
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se recuperan los datos pasados a traves del Intent desde el ProductAdapter
        val nombre = intent.getStringExtra("nombre")
        val marca = intent.getStringExtra("marca")
        val nutriscore = intent.getStringExtra("nutriscore")
        val cantidad = intent.getStringExtra("cantidad")
        val categoria = intent.getStringExtra("categoria")
        val imagenUrl = intent.getStringExtra("imagenUrl")
        val fechaCaducidad = intent.getStringExtra("fechaCaducidad")

        //Se muestran los datos en la interfaz
        binding.textNombre.text = nombre ?: "Producto"
        binding.textMarca.text = "Marca: ${marca ?: "-"}"
        binding.textNutriscore.text = "Nutriscore: ${nutriscore ?: "-"}"
        binding.textCantidad.text = "Cantidad: ${cantidad ?: "-"}"
        binding.textCategoria.text = "Categoría: ${categoria ?: "-"}"
        binding.textCaducidad.text = "Caduca: ${fechaCaducidad ?: "-"}"

        //Imagen
        val placeholderRes = when (categoria) {
            "Lácteos" -> R.drawable.placeholder_lacteos
            "Carnes" -> R.drawable.placeholder_carnes
            "Frutas" -> R.drawable.placeholder_frutas
            "Verduras" -> R.drawable.placeholder_verduras
            "Bebidas" -> R.drawable.placeholder_bebidas
            "Panadería" -> R.drawable.placeholder_panaderia
            else -> R.drawable.placeholder_otros
        }

        Glide.with(this)
            .load(imagenUrl)
            .placeholder(placeholderRes)
            .error(placeholderRes)
            .into(binding.imageViewProducto)

    }
}
