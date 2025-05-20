package com.example.dontwasteit.ui.statistics

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.dontwasteit.databinding.ActivityStatisticsBinding
import com.example.dontwasteit.viewmodel.ProductViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
                //binding.textTotal.text =
                    //"Total de productos: ${estadistica.productosConsumidos + estadistica.productosDesechados}"
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

                val entries = listOf(
                    BarEntry(0f, estadistica.productosConsumidos.toFloat()),
                    BarEntry(1f, estadistica.productosNoConsumidos.toFloat()),
                    BarEntry(2f, estadistica.productosDesechados.toFloat())
                )

                val dataSet = BarDataSet(entries, "Estadísticas").apply {
                    setColors(
                        ContextCompat.getColor(this@StatisticsActivity, android.R.color.holo_green_dark),
                        ContextCompat.getColor(this@StatisticsActivity, android.R.color.holo_orange_light),
                        ContextCompat.getColor(this@StatisticsActivity, android.R.color.holo_red_light)
                    )
                    valueTextSize = 14f
                }

                val barData = BarData(dataSet)
                binding.barChart.apply {
                    data = barData
                    description.isEnabled = false
                    xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(
                            listOf("Consumidos", "No consumidos", "Caducados")
                        )
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        setDrawGridLines(false)
                        textSize = 12f

                    }
                    axisLeft.textSize = 12f
                    axisRight.isEnabled = false
                    legend.isEnabled = false
                    animateY(1000)
                    setFitBars(true) // Asegura que las barras se ajusten
                    setExtraBottomOffset(16f) // Espacio extra por debajo para evitar corte de etiquetas
                    axisLeft.axisMinimum = 0f // Asegura que arranque desde cero
                    xAxis.yOffset = 10f // Da más aire entre el eje X y los labels
                    invalidate()
                }

            } else {
                binding.textSinDatos.visibility = View.VISIBLE
                binding.textConsumidos.visibility = View.GONE
                binding.textNoConsumidos.visibility = View.GONE
                binding.textCaducados.visibility = View.GONE
                binding.textPorcentaje.visibility = View.GONE
                binding.textComparacion.visibility = View.GONE
                binding.textCategoriaMasConsumida.visibility = View.GONE
                binding.barChart.visibility = View.GONE

            }
        }
    }

}
