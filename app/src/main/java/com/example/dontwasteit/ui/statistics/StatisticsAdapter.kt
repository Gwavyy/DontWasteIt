package com.example.dontwasteit.ui.statistics

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dontwasteit.data.database.entities.Estadistica
import com.example.dontwasteit.databinding.ItemStatisticsBinding

class StatisticsAdapter(private val list: List<Estadistica>) :
    RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemStatisticsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(stat: Estadistica) {
            binding.textFecha.text = "Date: ${stat.fecha}"
            binding.textResumen.text = """
                Consumed: ${stat.productosConsumidos}
                Discarded: ${stat.productosDesechados}
                Total: ${stat.productosConsumidos + stat.productosDesechados}
                Waste: %.1f%%
            """.trimIndent().format(stat.porcentajeDesechos)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStatisticsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
