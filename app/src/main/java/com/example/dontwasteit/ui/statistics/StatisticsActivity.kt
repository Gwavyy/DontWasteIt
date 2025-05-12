package com.example.dontwasteit.ui.statistics

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dontwasteit.databinding.ActivityStatisticsBinding
import com.example.dontwasteit.viewmodel.ProductViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private val viewModel: ProductViewModel by viewModels { ProductViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.products.collect { productos ->
                val total = productos.size
                val consumidos = productos.count { it.consumido }
                val noConsumidos = total - consumidos

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val hoy = LocalDate.now()

                val caducados = productos.count {
                    !it.consumido && try {
                        LocalDate.parse(it.fechaCaducidad, formatter).isBefore(hoy)
                    } catch (e: Exception) {
                        false
                    }
                }

                val porcentajeDesperdicio = if (total > 0) (caducados.toFloat() / total) * 100 else 0f

                // Mostrar en pantalla
                binding.textTotal.text = "Total de productos: $total"
                binding.textConsumidos.text = "Consumidos: $consumidos"
                binding.textNoConsumidos.text = "No consumidos: $noConsumidos"
                binding.textCaducados.text = "Caducados sin consumir: $caducados"
                binding.textPorcentaje.text = "Desperdicio: %.1f%%".format(porcentajeDesperdicio)
            }
        }
    }

}
