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
        // Inicia una corrutina para recuperar y mostrar las estadísticas del mes
        lifecycleScope.launch {
            val estadistica = viewModel.obtenerEstadisticaDelMes()
            // Si existen datos para el mes actual, se muestran en sus respectivos TextView
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

                // Compara el porcentaje con la media nacional y mostrar un mensaje
                val mensaje = if (estadistica.porcentajeDesechos <= 4.4f) {
                    "¡Enhorabuena! Estás por debajo de la media española de desperdicio (4.4%)"
                } else {
                    "Estás por encima de la media española de desperdicio (4.4%)"
                }
                binding.textComparacion.text = mensaje

                // Mostrar la categoría mas consumida en el mes o ninguna si no hay datos
                binding.textCategoriaMasConsumida.text =
                    "Categoría más consumida: ${estadistica.categoriaMasConsumida ?: "Ninguna"}"
            } else {
                binding.textTotal.text = "No hay datos de este mes todavía."
            }
        }
    }

}
