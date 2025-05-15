package com.example.dontwasteit.ui.statistics

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dontwasteit.databinding.ActivityStatisticsBinding
import com.example.dontwasteit.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private val viewModel: ProductViewModel by viewModels { ProductViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val estadistica = viewModel.obtenerEstadisticaDelMes()

            if (estadistica != null) {
                binding.textTotal.text =
                    "Total de productos: ${estadistica.productosConsumidos + estadistica.productosDesechados}"
                binding.textConsumidos.text =
                    "Consumidos: ${estadistica.productosConsumidos}"
                binding.textNoConsumidos.text =
                    "No consumidos: ${estadistica.productosNoConsumidos}"
                binding.textCaducados.text =
                    "Caducados sin consumir: ${estadistica.productosDesechados}"
                binding.textPorcentaje.text =
                    "Desperdicio: %.1f%%".format(estadistica.porcentajeDesechos)

                val mensaje = if (estadistica.porcentajeDesechos <= 12.9f) {
                    "¡Enhorabuena! Estás por debajo de la media española de desperdicio (12.9%)"
                } else {
                    "Estás por encima de la media española de desperdicio (12.9%)"
                }
                binding.textComparacion.text = mensaje

                binding.textCategoriaMasConsumida.text =
                    "Categoría más consumida: ${estadistica.categoriaMasConsumida ?: "Ninguna"}"
            } else {
                binding.textTotal.text = "No hay datos de este mes todavía."
            }
        }
    }

}
