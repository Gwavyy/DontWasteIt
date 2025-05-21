package com.example.dontwasteit.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dontwasteit.R
import com.example.dontwasteit.data.database.entities.Product
import com.example.dontwasteit.databinding.ItemProductBinding
import com.example.dontwasteit.ui.detail.ProductDetailActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Adaptador para la lista de productos en el RecyclerView principal.
 * Permite mostrar productos consumidos o no consumidos con diferentes comportamientos.
 */
class ProductAdapter(private val mostrarBotones: Boolean = true)
    : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback) {

    // Callbacks para cuando se pulsa en "Consumido" o "Eliminar"
    var onConsumidoClick: ((Product) -> Unit)? = null
    var onEliminarClick: ((Product) -> Unit)? = null


    //ViewHolder personalizado con ViewBinding.

    inner class ProductViewHolder(private val binding: ItemProductBinding)
        : RecyclerView.ViewHolder(binding.root) {

        //Enlaza los datos del producto con la vista.
        fun bind(producto: Product) {
            // Asignar textos
            binding.textNombre.text = producto.nombre
            binding.textNutriscore.text = "Nutriscore: ${producto.nutriscore ?: "unknown"}"
            binding.textCaducidad.text = "Caduca: ${producto.fechaCaducidad}"

            // Cargar imagen del producto
            val placeholderRes = when (producto.categoria) {
                "Lácteos" -> R.drawable.placeholder_lacteos
                "Carnes" -> R.drawable.placeholder_carnes
                "Frutas" -> R.drawable.placeholder_frutas
                "Verduras" -> R.drawable.placeholder_verduras
                "Bebidas" -> R.drawable.placeholder_bebidas
                "Panadería" -> R.drawable.placeholder_panaderia
                else -> R.drawable.placeholder_otros
            }

            Glide.with(binding.root.context)
                .load(producto.imagenUrl)
                .placeholder(placeholderRes)
                .error(placeholderRes)
                .into(binding.imageViewProducto)


            // Al hacer clic en el producto abre el detalle
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ProductDetailActivity::class.java).apply {
                    putExtra("nombre", producto.nombre)
                    putExtra("marca", producto.marca)
                    putExtra("nutriscore", producto.nutriscore)
                    putExtra("cantidad", producto.cantidad)
                    putExtra("categoria", producto.categoria)
                    putExtra("imagenUrl", producto.imagenUrl)
                    putExtra("fechaCaducidad", producto.fechaCaducidad)
                }
                context.startActivity(intent)
            }

            // Boton para marcar como consumido
            binding.buttonConsumido.setOnClickListener {
                val actualizado = producto.copy(consumido = true)
                onConsumidoClick?.invoke(actualizado)
            }

            // Boton para eliminar
            binding.buttonEliminar.setOnClickListener {
                onEliminarClick?.invoke(producto)
            }

            // Mostrar u ocultar botones dependiendo del contexto (consumidos o no)
            if (mostrarBotones) {
                try {
                    val fechaCad = LocalDate.parse(producto.fechaCaducidad, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaCad)

                    if (diasRestantes < 0) {
                        // Producto caducado: solo mostrar botón de eliminar
                        binding.buttonConsumido.visibility = View.GONE
                        binding.buttonEliminar.visibility = View.VISIBLE
                    } else {
                        // Producto aún válido: mostrar ambos botones
                        binding.buttonConsumido.visibility = View.VISIBLE
                        binding.buttonEliminar.visibility = View.VISIBLE
                    }

                } catch (e: Exception) {
                    // En caso de error al parsear fecha, mostrar ambos botones como fallback
                    binding.buttonConsumido.visibility = View.VISIBLE
                    binding.buttonEliminar.visibility = View.VISIBLE
                }
            } else {
                binding.buttonConsumido.visibility = View.GONE
                binding.buttonEliminar.visibility = View.GONE
            }


            // Indicador de caducidad con circulo de colores
            if (producto.consumido) {
                binding.circleIndicator.visibility = View.GONE
            } else {
                binding.circleIndicator.visibility = View.VISIBLE

                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val fechaCad = LocalDate.parse(producto.fechaCaducidad, formatter)
                    val diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaCad)

                    // Asignar color segun proximidad de caducidad
                    val colorDrawable = when {
                        diasRestantes <= 1 -> R.drawable.circle_red    // muy cerca de caducar || ya caducado
                        diasRestantes <= 3 -> R.drawable.circle_yellow // cerca de caducar
                        else -> R.drawable.circle_green                // en buen estado
                    }

                    binding.circleIndicator.setBackgroundResource(colorDrawable)

                } catch (e: Exception) {
                    // En caso de error, usa color verde por defecto
                    binding.circleIndicator.setBackgroundResource(R.drawable.circle_green)
                }
            }
        }
    }

    // Infla el layout del producto individual
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    // Asocia un producto a una vista
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        // Permite detectar si la lista ha cambiado eficientemente
        private val DiffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }
}
