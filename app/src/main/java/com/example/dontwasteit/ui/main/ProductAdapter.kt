package com.example.dontwasteit.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dontwasteit.data.database.entities.Product
import com.example.dontwasteit.databinding.ItemProductBinding
import com.example.dontwasteit.ui.detail.ProductDetailActivity

class ProductAdapter(private val mostrarBotones: Boolean = true) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback) {

    var onConsumidoClick: ((Product) -> Unit)? = null
    var onEliminarClick: ((Product) -> Unit)? = null

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Product) {
            binding.textNombre.text = producto.nombre
            binding.textNutriscore.text = "Nutriscore: ${producto.nutriscore ?: "?"}"
            binding.textCaducidad.text = "Caduca: ${producto.fechaCaducidad}"

            Glide.with(binding.root.context)
                .load(producto.imagenUrl)
                .into(binding.imageViewProducto)

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
            binding.buttonConsumido.setOnClickListener {
                val actualizado = producto.copy(consumido = true)
                onConsumidoClick?.invoke(actualizado)
            }
            binding.buttonEliminar.setOnClickListener {
                onEliminarClick?.invoke(producto)
            }
            if (mostrarBotones) {
                binding.buttonConsumido.visibility = View.VISIBLE
                binding.buttonEliminar.visibility = View.VISIBLE

                binding.buttonConsumido.setOnClickListener {
                    val actualizado = producto.copy(consumido = true)
                    onConsumidoClick?.invoke(actualizado)
                }

                binding.buttonEliminar.setOnClickListener {
                    onEliminarClick?.invoke(producto)
                }
            } else {
                binding.buttonConsumido.visibility = View.GONE
                binding.buttonEliminar.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
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
